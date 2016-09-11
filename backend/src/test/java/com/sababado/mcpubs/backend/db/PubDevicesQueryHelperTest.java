package com.sababado.mcpubs.backend.db;

import com.google.api.server.spi.response.BadRequestException;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.models.PubDevices;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testInsertPubDevicesRecord() throws UnrecognizedPubException, BadRequestException {
        Pub pub = new Pub("MCO AAA4990.342B", "A readable title yeahyeah", true, Pub.MCO);
        pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);
        String newToken = "AAA567980ghjklr7689";
        Device device = DeviceQueryHelper.updateToken(connection, null, newToken);

        PubDevices pubDevices = PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
        assertTrue(pubDevices.getId() > 0);
        pubDevicesIdsToCleanup.add(pubDevices.getId());
        assertEquals(device, pubDevices.getDevice());
        assertEquals(pub, pubDevices.getPub());

        PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
        PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
        PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
        List<PubDevices> pubDevicesList = DbUtils.getList(connection, PubDevices.class, " where deviceId=" + device.getId() + " and pubId=" + pub.getId());
        assertEquals(1, pubDevicesList.size());
    }

    @Test
    public void testDeleteRecord() throws UnrecognizedPubException, BadRequestException {
        Pub pub = new Pub("MCO AAA4990.342B", "A readable title yeahyeah", true, Pub.MCO);
        pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);
        String newToken = "AAA567980ghjklr7689";
        Device device = DeviceQueryHelper.updateToken(connection, null, newToken);
        PubDevices pubDevices = PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());

        assertTrue(PubDevicesQueryHelper.deletePubDevicesRecord(connection, device.getDeviceToken(), pub.getId()));
        assertNull(PubDevicesQueryHelper.getPubDevicesRecord(connection, pubDevices.getId()));
    }

    @Test
    public void testCleanupUnwatchedPubs() throws UnrecognizedPubException, BadRequestException {
        Pub pub = new Pub("MCO AAA4990.342B", "A readable title yeahyeah", true, Pub.MCO);
        pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);
        String newToken = "AAA567980ghjklr7689";
        Device device = DeviceQueryHelper.updateToken(connection, null, newToken);
        PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());

        pub = new Pub("MCO AAA4990.343C", "A readable title yeahyeah", true, Pub.MCO);
        PubQueryHelper.insertOrUpdateRecord(connection, pub);
        pub = new Pub("MCO AAA4990.344D", "A readable title yeahyeah", true, Pub.MCO);
        pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);

        assertEquals(2, PubDevicesQueryHelper.cleanupUnwatchedPubs(connection));
        assertEquals(0, PubDevicesQueryHelper.cleanupUnwatchedPubs(connection));
    }

    @After
    public void cleanup() {
        try {
            if (pubDevicesIdsToCleanup.size() > 0) {
                String cleanupIds = Arrays.toString(pubDevicesIdsToCleanup.toArray(new Long[pubDevicesIdsToCleanup.size()]));
                cleanupIds = cleanupIds.replace("[", "(").replace("]", ")");
                connection.prepareStatement("DELETE FROM PubDevices WHERE ID IN " + cleanupIds + ";").execute();
            }
            connection.prepareStatement("DELETE FROM Device WHERE " + Device.DEVICE_TOKEN + " LIKE 'AAA%';").execute();
            connection.prepareStatement("DELETE FROM PUB WHERE " + Pub.FULL_CODE + " LIKE 'AAA%';").execute();
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbUtils.closeConnection(connection);
    }
}
