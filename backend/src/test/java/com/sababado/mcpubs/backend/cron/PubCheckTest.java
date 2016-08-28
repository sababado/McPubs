package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.factory.FactoryHelper;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.utils.PubUtils.CompareResult;
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

    @Test
    public void commitUpdatesTest() {
        List<Tuple<Pub, Pub, CompareResult>> changes = new ArrayList<>();
        try {
            // Setup changes.
            // deleted Pub
            Pub existingPub = new Pub("MCO AAA6600.42A", "A readable title", true, 2005);
            existingPub = PubQueryHelper.insertRecordIfNonExistent(connection, existingPub);
            Pub newPub = new Pub("MCO AAA6600.42A", "A readable title", false, 2005);
            newPub.setId(existingPub.getId());
            changes.add(new Tuple<>(existingPub, newPub, CompareResult.DELETED));

            // updated pub
            existingPub = new Pub("MCO AAA9900.42A", "A readable title", true, 2005);
            existingPub = PubQueryHelper.insertRecordIfNonExistent(connection, existingPub);
            newPub = new Pub("MCO AAA9900.42B", "A readable title yup", true, 2005);
            newPub.setId(existingPub.getId());
            changes.add(new Tuple<>(existingPub, newPub, CompareResult.UPDATE));

            // updated, but deleted pub
            existingPub = new Pub("MCO AAA9800.42B", "A readable title", true, 2005);
            existingPub = PubQueryHelper.insertRecordIfNonExistent(connection, existingPub);
            newPub = new Pub("MCO AAA9800.42C", "A new readable title", false, 2005);
            newPub.setId(existingPub.getId());
            changes.add(new Tuple<>(existingPub, newPub, CompareResult.UPDATE_BUT_DELETED));
            // end setting up changes.
        } catch (UnrecognizedPubException e) {
            e.printStackTrace();
            fail();
        }

        PubCheck.commitUpdates(connection, changes);
        for (Tuple<Pub, Pub, CompareResult> tuple : changes) {
            long id = tuple.one.getId();
            Pub newPub = PubQueryHelper.getPubRecord(connection, id, null, null);
            tuple.two.setId(id);

            // verify the timestamp has changed.
            assertTrue(newPub.getLastUpdated() > tuple.two.getLastUpdated());
            // now verify the rest of the objects.
            tuple.two.setLastUpdated(newPub.getLastUpdated());
            assertEquals(tuple.two, newPub);
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
