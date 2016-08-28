package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.factory.Factory;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.rss.PubCheckParser;
import com.sababado.mcpubs.backend.utils.PubSorter;
import com.sababado.mcpubs.backend.utils.StringUtils;

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
        Connection connection = DbUtils.openConnection();

        List<String> distinctRootCodes = PubQueryHelper.getDistinctRootCodes(connection);

        int count = distinctRootCodes == null ? 0 : distinctRootCodes.size();
        for (int i = 0; i < count; i++) {
            String rootCode = distinctRootCodes.get(i);
            List<Pub> pubs = getPubsFromSearch(rootCode);
            ///TODO
        }

        DbUtils.closeConnection(connection);
    }

    // TODO only check pubs that haven't been checked in the past month.

    List<Pub> getPubsFromSearch(String pubRootCode) {
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
                List<Pub> newPubList = PubCheckParser.parseSearchResults(document);
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
