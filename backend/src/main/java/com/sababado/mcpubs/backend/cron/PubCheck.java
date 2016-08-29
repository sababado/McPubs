package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.factory.Factory;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.rss.PubCheckParser;
import com.sababado.mcpubs.backend.utils.PubSorter;
import com.sababado.mcpubs.backend.utils.PubUtils;
import com.sababado.mcpubs.backend.utils.PubUtils.CompareResult;
import com.sababado.mcpubs.backend.utils.StringUtils;
import com.sababado.mcpubs.backend.utils.Tuple;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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
    private static final String SEARCH_URL = "http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Search/%s/?Page=1";

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
        List<String> distinctRootCodes = PubQueryHelper.getDistinctRootCodes(connection, pubType);

        int count = distinctRootCodes == null ? 0 : distinctRootCodes.size();
        for (int i = 0; i < count; i++) {
            List<Tuple<Pub, Pub, CompareResult>> changesList = new ArrayList<>();
            String rootCode = distinctRootCodes.get(i);
            List<Pub> pubsFromSearch = getPubsFromSearch(rootCode, pubType);
            // We're assuming the pubsFromSearch list is sorted by fullCode and version, ascending.
            if (pubsFromSearch.size() > 0) {
                // only continue if we found any pubs.
                List<Pub> watchedPubs = DbUtils.getList(connection, Pub.class,
                        PubQueryHelper.ACTIVE_PUB_WHERE_CLAUSE +
                                " and " + Pub.ROOT_CODE + "='" + rootCode + "'" +
                                " and " + Pub.PUB_TYPE + "=" + pubType + " ");

                //get most recent pub from a subset of pubs with the same full code.
                int pfsCounter = 0;
                int pfsSize = pubsFromSearch.size();

                do {
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
                    Pub watchedPub = PubUtils.findPubByFullCode(watchedPubs, currentFullCode);
                    if (watchedPub != null) {
                        CompareResult compareResult = comparePub(watchedPub, mostRecentPub);
                        if (compareResult != CompareResult.NO_CHANGE) {
                            mostRecentPub.setId(watchedPub.getId());
                            changesList.add(new Tuple<>(watchedPub, mostRecentPub, compareResult));
                        }
                    }

                } while (pfsCounter < pfsSize);
            }

            // commit updates
            commitUpdates(connection, changesList);

            // send updates.
            //TODO
        }

        DbUtils.closeConnection(connection);
    }

    /**
     * Compare two pubs.
     *
     * @param existingPub
     * @param newPub
     * @return Return a {@link CompareResult CompareStatus}
     * relating to the outcome of the compare.
     */
    static CompareResult comparePub(Pub existingPub, Pub newPub) {
        // both pubs with no version are considered the same, active flag needs to be checked
        // new pub with version and watched pub without version is considered updated.
        // new pub with higher version than watched pub is considered updated.

        final boolean isDeleted = existingPub.isActive() && !newPub.isActive();

        if (existingPub.getVersion() == null && newPub.getVersion() == null) {
            return isDeleted ? CompareResult.DELETED : CompareResult.NO_CHANGE;
        }

        if (existingPub.getVersion() == null && newPub.getVersion() != null) {
            return isDeleted ? CompareResult.UPDATE_BUT_DELETED : CompareResult.UPDATE;
        }

        if (existingPub.getVersion() != null && newPub.getVersion() != null) {
            boolean updated = existingPub.getVersion().compareTo(newPub.getVersion()) < 0;
            if (!updated) return isDeleted ? CompareResult.DELETED : CompareResult.NO_CHANGE;
            return isDeleted ? CompareResult.UPDATE_BUT_DELETED : CompareResult.UPDATE;
        }

        // this would happen if we're comparing an existing pub with a version higher than that of
        // a new pub. This shouldn't happen.
        return CompareResult.NO_CHANGE;
    }

    static void commitUpdates(Connection connection, List<Tuple<Pub, Pub, CompareResult>> changes) {
        // save changes to the DB. Assuming the changes already have existing IDs associated to them.
        int changeCount = changes.size();
        List<Pub> changedPubs = new ArrayList<>(changeCount);
        for (int i = 0; i < changeCount; i++) {
            changedPubs.add(changes.get(i).two);
        }
        PubQueryHelper.batchUpdate(connection, changedPubs);

        // TODO send notifications.
    }

    // TODO only check pubs that haven't been checked in the past month.

    List<Pub> getPubsFromSearch(String pubRootCode, int pubType) {
        String searchUrl = String.format(SEARCH_URL, pubRootCode);
        List<Pub> pubList = new ArrayList<>();
        String[] searchPages = null;
        int pageCounter = 0;
        do {
            try {
                // Get the page
                Document document = Factory.getNetworkProvider().doNetworkCall(searchUrl);
                if (searchPages == null) {
                    searchPages = PubCheckParser.parsePageLinks(document);
                }

                // Get all the pubs on this page.
                List<Pub> newPubList = PubCheckParser.parseSearchResults(document, pubType);
                pubList.addAll(newPubList);

                if (searchPages != null && pageCounter + 1 < searchPages.length) {
                    // If there are anymore pages to check then get ready for that work.
                    searchUrl = searchPages[++pageCounter];
                } else {
                    // Nothing more to search, we're done here.
                    searchUrl = null;
                }
            } catch (IOException e) {
                _logger.severe(e.getMessage());
            }
        } while (!StringUtils.isEmptyOrWhitespace(searchUrl));

        // sort pubs by fullCode
        PubSorter.sortByFullCode(pubList);

        return pubList;
    }
}
