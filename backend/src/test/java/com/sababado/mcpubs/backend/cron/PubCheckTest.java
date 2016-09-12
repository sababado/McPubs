package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.factory.FactoryHelper;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.utils.PubUtils.UpdateStatus;
import com.sababado.mcpubs.backend.utils.Tuple;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by robert on 8/27/16.
 */
public class PubCheckTest {
    private Connection connection;

    @Before
    public void setup() {
        FactoryHelper.setMockNetworkHelper();
        connection = DbUtils.openConnection();
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
            assertEquals(UpdateStatus.DELETED, PubCheck.comparePub(existingPub, newPub));

            // Pub removed but no change.
            existingPub = new Pub("MCO 3500.42", "A readable title", false, 2005);
            newPub = new Pub("MCO 3500.42", "A readable title", false, 2005);
            assertEquals(UpdateStatus.NO_CHANGE, PubCheck.comparePub(existingPub, newPub));
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
            assertEquals(UpdateStatus.UPDATED, PubCheck.comparePub(existingPub, newPub));

            // Existing pub with version
            existingPub = new Pub("MCO 3500.42A", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            assertEquals(UpdateStatus.UPDATED, PubCheck.comparePub(existingPub, newPub));

            // Existing pub with version, no update
            existingPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            assertEquals(UpdateStatus.NO_CHANGE, PubCheck.comparePub(existingPub, newPub));

            // Updated, but the new pub is deleted.
            existingPub = new Pub("MCO 3500.42B", "A readable title", true, 2005);
            newPub = new Pub("MCO 3500.42C", "A readable title", false, 2005);
            assertEquals(UpdateStatus.UPDATED_BUT_DELETED, PubCheck.comparePub(existingPub, newPub));

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
            assertEquals(UpdateStatus.DELETED, PubCheck.comparePub(existingPub, newPub));
        } catch (UnrecognizedPubException e) {
            e.printStackTrace();
        }
    }

    @After
    public void teardown() {
        try {
            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " LIKE 'AAA%';").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbUtils.closeConnection(connection);
        FactoryHelper.removeMockNetworkHelper();
    }
}
