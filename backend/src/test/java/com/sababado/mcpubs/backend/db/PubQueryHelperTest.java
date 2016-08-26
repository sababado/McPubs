package com.sababado.mcpubs.backend.db;

import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Pub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by robert on 8/25/16.
 */
public class PubQueryHelperTest {
    Connection connection = null;
    private static final String DUMMY_NAME = "AAA4200.43";

    @Before
    public void setup() {
        try {
            connection = DbUtils.openConnection();
            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " in ('" + DUMMY_NAME + "', '3-12');").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsertPub() {
        Pub existingRecord = PubQueryHelper.getPubRecord(connection, null, DUMMY_NAME, true);
        assertNull(existingRecord);

        Pub newPub = new Pub();
        newPub.setFullCode(DUMMY_NAME);
        newPub.setRootCode("AAA4200");
        newPub.setActive(true);
        Pub newPubRecord = PubQueryHelper.insertRecordIfNonExistent(connection, newPub);
        assertNotNull(newPubRecord);
        assertNotNull(newPubRecord.getId());
        assertTrue(newPubRecord.getId() > 0);
        newPub.setId(newPubRecord.getId());
        assertEquals(newPub, newPubRecord);

        // record shouldn't be returned if looking for an inactive one.
        existingRecord = PubQueryHelper.getPubRecord(connection, null, DUMMY_NAME, false);
        assertNull(existingRecord);

        // record should be returned for an active one.
        existingRecord = PubQueryHelper.getPubRecord(connection, null, DUMMY_NAME, true);
        assertNotNull(existingRecord);
        assertEquals(newPubRecord.getId(), existingRecord.getId());
        assertEquals(newPubRecord.getFullCode(), existingRecord.getFullCode());
        assertEquals(newPubRecord.getRootCode(), existingRecord.getRootCode());
        assertEquals(newPubRecord.isActive(), existingRecord.isActive());
        assertTrue(existingRecord.getLastUpdated() > newPubRecord.getLastUpdated());

        //Inserting a new record with the same id should return the same result.
        newPubRecord = PubQueryHelper.insertRecordIfNonExistent(connection, newPubRecord);
        assertEquals(newPubRecord.getId(), existingRecord.getId());
    }

    @After
    public void cleanup() {
        try {
//            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " in ('" + DUMMY_NAME + "', '3-12');").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbUtils.closeConnection(connection);
    }
}
