package com.sababado.mcpubs.backend.rss;

import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.utils.PubUtils;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheckParser {
    private static final Logger _logger = Logger.getLogger(PubCheckParser.class.getName());
    private static final String PAGE_STR = "Page=";

    /**
     * Send the document for the HTML page that has a list of pubs in it.
     *
     * @param document Document of the HTML page.
     * @param pubType  Type of pubs we're looking at.
     * @return A list of pubs, or none if none exist or none were recognized.
     */
    public static List<Pub> parseSearchResults(Document document, int pubType) {
        Elements pubList = document.getElementsByClass("alist-pub")
                .first()
                .getAllElements();
        int listCount = pubList.size();
        ArrayList<Pub> pubs = new ArrayList<>(listCount);

        for (int i = 0; i < listCount; i++) {
            Element element = pubList.get(i);
            if (element.hasClass("item")) {
                // Got a pub!!!
                Element titleElement = element.getElementsByClass("title").first();
                String title = titleElement.getElementsByTag("a").text();
                // Get the 3rd span for the isActive text.
                String isActiveStr = element.getElementsByTag("span")
                        .get(2)
                        .text();
                boolean isActive = PubUtils.parseStatus(isActiveStr);
                String readableTitle = element.getElementsByTag("p").text();
                try {
                    //Try adding this newly parsed pub
                    pubs.add(new Pub(title, readableTitle, isActive, pubType));
                } catch (UnrecognizedPubException e) {
                    _logger.warning(e.getMessage());
                }
            }
        }

        return pubs;
    }

    /**
     * Send the document for the HTML page that has pagination.
     *
     * @param document Pagination document.
     * @return An array of links, one item for each page.
     */
    public static String[] parsePageLinks(Document document) {
        Elements paginationElements = document.getElementsByClass("article-task-bar")
                .first()
                .getElementsByClass("pagination");
        String[] pageLinks;
        if (paginationElements.size() == 0) {
            pageLinks = new String[0];
        } else {
            Elements elements = paginationElements.first().getElementsByTag("li");

            int pageCount = elements.size();
            pageLinks = new String[pageCount];

            if (pageCount > 0) {
                String lastLink = pageLinks[pageCount - 1] =
                        elements.last()
                                .getElementsByTag("a")
                                .first()
                                .attr("abs:href");
                String lastPageStr = PAGE_STR + pageCount;
                // Create a link for all other pages, except the last one since it is already set.
                for (int i = 1; i < pageCount; i++) {
                    pageLinks[i - 1] = lastLink.replace(lastPageStr, PAGE_STR + i);
                }
            }
        }

        return pageLinks;
    }
}
