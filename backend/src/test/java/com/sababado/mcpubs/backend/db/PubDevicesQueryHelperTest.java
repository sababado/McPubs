package com.sababado.mcpubs.backend.db;

import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.models.Pub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by robert on 9/1/16.
 */
public class PubDevicesQueryHelperTest {
    Connection connection = null;
    List<Long> pubDevicesIdsToCleanup = new ArrayList<>();

    @Before
    public void setup() {
        try {
            connection = DbUtils.openConnection();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsertPubDevicesRecord() {

    }

    @After
    public void cleanup() {
        try {
            String cleanupIds = Arrays.toString(pubDevicesIdsToCleanup.toArray(new Long[pubDevicesIdsToCleanup.size()]));
            cleanupIds = cleanupIds.replace("[", "(").replace("]", ")");
            connection.prepareStatement("DELETE FROM PubDevices WHERE ID IN " + cleanupIds + ";").execute();

            connection.prepareStatement("DELETE FROM Device WHERE " + Device.DEVICE_TOKEN + " LIKE 'AAA%';").execute();
            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " LIKE 'AAA%';").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbUtils.closeConnection(connection);
    }
}
