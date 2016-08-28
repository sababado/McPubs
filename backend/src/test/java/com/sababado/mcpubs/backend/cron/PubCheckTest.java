package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.factory.FactoryHelper;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.utils.PubUtils.CompareResult;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheckTest {
    @Before
    public void setup() {
        FactoryHelper.setMockNetworkHelper();
    }

    @Test
    public void testGetPubsFromSearch() {
        String rootCode = "3500";
        PubCheck pubCheck = new PubCheck();
        List<Pub> pubs = pubCheck.getPubsFromSearch(rootCode, Pub.MCO);
        assertNotNull(pubs);
        assertEquals(57, pubs.size());
        //checking inequality of random elements in list.
        assertNotSame(pubs.get(0), pubs.get(25));
        assertNotSame(pubs.get(22), pubs.get(51));

        // test sort
    }

    @Test
    public void comparePubNoVersionTest() {
        try {
            // Pub removed
            Pub existingPub = new Pub("MCO 3500.42", "A readable title", true, 2005);
            Pub newPub = new Pub("MCO 3500.42", "A readable title", false, 2005);
            assertEquals(CompareResult.DELETED, PubCheck.comparePub(existingPub, newPub));

            // Pub removed but no change.
            existingPub = new Pub("MCO 3500.42", "A readable title", false, 2005);
            newPub = new Pub("MCO 3500.42", "A readable title", false, 2005);
            assertEquals(CompareResult.NO_CHANGE, PubCheck.comparePub(existingPub, newPub));
        } catch (UnrecognizedPubException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparePubUpdateTest() {
        try {
            // Existing pub with no version
            Pub existingPub = new Pub("MCO 3500.42", "A readable title", true, 2005);
            Pub newPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            assertEquals(CompareResult.UPDATE, PubCheck.comparePub(existingPub, newPub));

            // Existing pub with version
            existingPub = new Pub("MCO 3500.42A", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            assertEquals(CompareResult.UPDATE, PubCheck.comparePub(existingPub, newPub));

            // Existing pub with version, no update
            existingPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            assertEquals(CompareResult.NO_CHANGE, PubCheck.comparePub(existingPub, newPub));

            // Updated, but the new pub is deleted.
            existingPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42C", "A readable title", false, 2005);
            assertEquals(CompareResult.UPDATE_BUT_DELETED, PubCheck.comparePub(existingPub, newPub));

        } catch (UnrecognizedPubException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparePubRemovedTest() {
        try {
            // Pub removed
            Pub existingPub = new Pub("MCO 3500.42A", "A readable title", true, 2005);
            Pub newPub = new Pub("MCO 3500.42A", "A readable title", false, 2005);
            assertEquals(CompareResult.DELETED, PubCheck.comparePub(existingPub, newPub));
        } catch (UnrecognizedPubException e) {
            e.printStackTrace();
        }
    }

    @After
    public void teardown() {
        FactoryHelper.removeMockNetworkHelper();
    }
}
