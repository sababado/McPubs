package com.sababado.mcpubs.backend.rss;

import com.sababado.mcpubs.backend.FileUtils;
import com.sababado.mcpubs.backend.models.Pub;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheckParserTest {
    @Test
    public void parseMcoPubs() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("mco_results.html"), "UTF-8");
            List<Pub> pubs = PubCheckParser.parseSearchResults(document);
            assertNotNull(pubs);
            assertEquals(25, pubs.size());
            assertEquals("MCO 3500.24A", pubs.get(0).getTitle());
            assertEquals("POLICY FOR THE FEDERAL BUREAU OF INVESTIGATION (FBI) TRAINING ASSISTANCE TO THE MARINE CORPS", pubs.get(0).getReadableTitle());
            assertEquals(false, pubs.get(0).isActive());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseMcopPubs() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("mcop_results.html"), "UTF-8");
            List<Pub> pubs = PubCheckParser.parseSearchResults(document);
            Pub pub = pubs.get(1);
            assertNotNull(pubs);
            assertEquals(25, pubs.size());
            assertEquals("MCO P4030.21D", pub.getTitle());
            assertEquals("PACKAGING OF MATERIEL - PACKING", pub.getReadableTitle());
            assertEquals(true, pub.isActive());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseDoctrinePubs() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("doctrine_results.html"), "UTF-8");
            List<Pub> pubs = PubCheckParser.parseSearchResults(document);
            assertNotNull(pubs);
            assertEquals(25, pubs.size());
            assertEquals("MCIP 3-03Di (Formerly MCIP 3-33.03)", pubs.get(0).getTitle());
            assertEquals("SECURITY COOPERATION", pubs.get(0).getReadableTitle());
            assertEquals(true, pubs.get(0).isActive());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseNoPubs() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("no_items_results.html"), "UTF-8");
            List<Pub> pubs = PubCheckParser.parseSearchResults(document);
            assertNotNull(pubs);
            assertEquals(0, pubs.size());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parsePagination() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("pagination.html"), "UTF-8");
            String[] pageLinks = PubCheckParser.parsePageLinks(document);
            assertEquals(7, pageLinks.length);
            assertEquals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2006/?Page=1", pageLinks[0]);
            assertEquals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2006/?Page=2", pageLinks[1]);
            assertEquals("http://www.marines.mil/News/Publications/ELECTRONIC-LIBRARY/Custompubtype/2006/?Page=7", pageLinks[6]);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseNoPagination() {
        try {
            Document document = Jsoup.parse(FileUtils.readFile("no_pagination.html"), "UTF-8");
            String[] pageLinks = PubCheckParser.parsePageLinks(document);
            assertEquals(0, pageLinks.length);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
