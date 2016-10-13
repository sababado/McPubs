package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.factory.Factory;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.models.PubNotification;
import com.sababado.mcpubs.backend.rss.PubCheckParser;
import com.sababado.mcpubs.backend.utils.Messaging;
import com.sababado.mcpubs.backend.utils.PubSorter;
import com.sababado.mcpubs.backend.utils.PubUtils;
import com.sababado.mcpubs.backend.utils.PubUtils.UpdateStatus;
import com.sababado.mcpubs.backend.utils.StringUtils;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheck extends HttpServlet {
    private static final Logger _logger = Logger.getLogger(PubCheck.class.getName());
    private static final String SEARCH_URL = "http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/%d/Search/%s/?Page=1";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkPubs(Pub.MCO);
        checkPubs(Pub.MCO_P);
        checkPubs(Pub.NAVMC);
        checkPubs(Pub.NAVMC_DIR);
    }

    /**
     * Given a pub type, Run a search on every distinct <code>rootCode</code> that is being watched (aka in the db).
     * Look through the search results to find the latest updates or changes.
     * Compare that against a similar record from our DB. If there are changes then user's watching that pub
     * need to be notified. Ignore any search results that aren't currently being 'watched'.
     *
     * @param pubType
     */
    void checkPubs(int pubType) {
        Connection connection = DbUtils.openConnection();
        String where = "(" + Pub.LAST_UPDATED + " IS NULL or" + Pub.LAST_UPDATED + "= '" + getLastMonthDate() + "')";
        List<String> distinctRootCodes = PubQueryHelper.getDistinctRootCodes(connection, pubType, where);

        int count = distinctRootCodes == null ? 0 : distinctRootCodes.size();
        _logger.info("Checking pubs (type " + pubType + ") with " + count + " distinct code(s).");
        // changesList includes all pubs with status updates (updated, deleted, etc)
        List<Pub> changesList = new ArrayList<>();
        // dataChangedPubs includes all pubs with new or updated information. Not necessarily a status update.
        List<Pub> dataChangedPubs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String rootCode = distinctRootCodes.get(i);
            List<Pub> pubsFromSearch = getPubsFromSearch(rootCode, pubType);
            // We're assuming the pubsFromSearch list is sorted by fullCode and version, ascending.
            if (pubsFromSearch.size() > 0) {
                // only continue if we found any pubs.
                List<Pub> watchedPubs = DbUtils.getList(connection, Pub.class,
                        "where " + PubQueryHelper.ACTIVE_PUB_WHERE_CLAUSE +
                                " and " + Pub.ROOT_CODE + "='" + rootCode + "'" +
                                " and " + Pub.PUB_TYPE + "=" + pubType + " ");

                //get most recent pub from a subset of pubs with the same full code.
                int pfsCounter = 0;
                int pfsSize = pubsFromSearch.size();

                do {
                    // mostRecentPub is the pub just now parsed from the server
                    Pub mostRecentPub = pubsFromSearch.get(pfsCounter);
                    String currentFullCode = mostRecentPub.getFullCode();
                    for (; pfsCounter < pfsSize; pfsCounter++) {
                        Pub pub = pubsFromSearch.get(pfsCounter);
                        if (currentFullCode.equalsIgnoreCase(pub.getFullCode())) {
                            mostRecentPub = pub;
                        } else {
                            break;
                        }
                    }

                    // compare version against watched pub
                    // watchedPub is the old pub, currently saved in the db.
                    Pub watchedPub = PubUtils.findPubByFullCode(watchedPubs, currentFullCode);

                    if (watchedPub != null) {
                        UpdateStatus updateStatus = comparePub(watchedPub, mostRecentPub);
                        if (updateStatus != UpdateStatus.NO_CHANGE) {
                            mostRecentPub.setOldTitle(watchedPub.getTitle());
                            mostRecentPub.setUpdateStatus(updateStatus.ordinal());
                            mostRecentPub.setId(watchedPub.getId());
                            changesList.add(mostRecentPub);
                        } else if (!PubUtils.pubInfoEquals(watchedPub, mostRecentPub)) {
                            // update status = no change, but the info has updated
                            mostRecentPub.setId(watchedPub.getId());
                            dataChangedPubs.add(mostRecentPub);
                        }
                    }

                } while (pfsCounter < pfsSize);
            }

        }
        int numUpdates = changesList.size();
        int numDataChangesSize = dataChangedPubs.size();
        _logger.info("----Processing " + numUpdates + " updates(s).\n" +
                "----Processing " + numDataChangesSize + " data change(s).");
        if (numUpdates > 0) {
            // commit updates
            PubQueryHelper.batchUpdate(connection, changesList);
            // send updates.
            sendNotifications(changesList);
        }
        if (numDataChangesSize > 0) {
            PubQueryHelper.batchUpdate(connection, dataChangedPubs);
        }

        DbUtils.closeConnection(connection);
    }

    static String getLastMonthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return StringUtils.SQL_FORMAT.format(calendar.getTime());
    }

    /**
     * Compare two pubs.
     *
     * @param existingPub
     * @param newPub
     * @return Return a {@link UpdateStatus CompareStatus}
     * relating to the outcome of the compare.
     */
    static UpdateStatus comparePub(Pub existingPub, Pub newPub) {
        // both pubs with no version are considered the same, active flag needs to be checked
        // new pub with version and watched pub without version is considered updated.
        // new pub with higher version than watched pub is considered updated.

        final boolean isDeleted = existingPub.isActive() && !newPub.isActive();

        if (existingPub.getVersion() == null && newPub.getVersion() == null) {
            return isDeleted ? UpdateStatus.DELETED : UpdateStatus.NO_CHANGE;
        }

        if (existingPub.getVersion() == null && newPub.getVersion() != null) {
            return isDeleted ? UpdateStatus.UPDATED_BUT_DELETED : UpdateStatus.UPDATED;
        }

        if (existingPub.getVersion() != null && newPub.getVersion() != null) {
            boolean updated = existingPub.getVersion().compareTo(newPub.getVersion()) < 0;
            if (!updated) return isDeleted ? UpdateStatus.DELETED : UpdateStatus.NO_CHANGE;
            return isDeleted ? UpdateStatus.UPDATED_BUT_DELETED : UpdateStatus.UPDATED;
        }

        // this would happen if we're comparing an existing pub with a version higher than that of
        // a new pub. This shouldn't happen.
        return UpdateStatus.NO_CHANGE;
    }

    private void sendNotifications(List<Pub> changes) {
        int size = changes.size();
        for (int i = 0; i < size; i++) {
            Pub pub = changes.get(i);
            String topic = pub.getFullCode();
            PubNotification pubNotification = new PubNotification(pub, topic, true);
            Messaging.sendMessage(pubNotification);
        }
    }

    List<Pub> getPubsFromSearch(String pubRootCode, int pubType) {
        String searchUrl = String.format(Locale.US, SEARCH_URL, pubType, pubRootCode);
        List<Pub> pubList = new ArrayList<>();
        String[] searchPages = null;
        int pageCounter = 0;
        do {
            try {
                // Get the page
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                Document document = Factory.getNetworkProvider().doNetworkCall(searchUrl);
                if (searchPages == null) {
                    searchPages = PubCheckParser.parsePageLinks(document);
                }

                // Get all the pubs on this page.
                List<Pub> newPubList = PubCheckParser.parseSearchResults(document, pubType);
                pubList.addAll(newPubList);

            } catch (IOException e) {
                _logger.severe(e.getMessage() + "\n" + searchUrl);
            }
            if (searchPages != null && pageCounter + 1 < searchPages.length) {
                // If there are anymore pages to check then get ready for that work.
                searchUrl = searchPages[++pageCounter];
            } else {
                // Nothing more to search, we're done here.
                searchUrl = null;
            }
        } while (!StringUtils.isEmptyOrWhitespace(searchUrl));

        // sort pubs by fullCode
        PubSorter.sortByFullCode(pubList);

        return pubList;
    }
}
