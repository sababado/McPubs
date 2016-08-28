package com.sababado.mcpubs.backend.db;

import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Pub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    @Test
    public void testGetDistinctRootCodes() {
        try {
            // insert dummy data
            connection.prepareStatement("INSERT INTO `mcPubsDev`.`Pub`\n" +
                    "(`fullCode`,`rootCode`,`code`,`version`,`isActive`,`pubType`)\n" +
                    "VALUES\n" +
                    "('AAA4200.43','AAA4200',43,'B',true, 2005),\n" +
                    "('AAA4200.19','AAA4200',19,'B',true, 2005),\n" +
                    "('AAA4500.10','AAA4500',10,'A',false, 2005),\n" +
                    "('AAA4500.11','AAA4500',11,'A',true, 2005),\n" +
                    "('AAA3500.99','AAA3500',99,'D',true, 2005),\n" +
                    "('AAA3200.18','AAA3200',18,'C',false, 2005),\n" +
                    "('AAAP3200.18','AAAP3200',18,'C',true, 2006);")
                    .execute();

            List<String> actual = PubQueryHelper.getDistinctRootCodes(connection, Pub.MCO, Pub.ROOT_CODE + " like 'AAA%'");
            List<String> expected = new ArrayList<>();
            expected.add("AAA3500");
            expected.add("AAA4200");
            expected.add("AAA4500");
            assertEquals(expected, actual);

            actual = PubQueryHelper.getDistinctRootCodes(connection, Pub.MCO_P, Pub.ROOT_CODE + " like 'AAA%'");
            expected.clear();
            expected.add("AAAP3200");
            assertEquals(expected, actual);
        } catch (SQLException e) {
            fail();
        }
    }

    @After
    public void cleanup() {
        try {
            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " LIKE 'AAA%';").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbUtils.closeConnection(connection);
    }
}
