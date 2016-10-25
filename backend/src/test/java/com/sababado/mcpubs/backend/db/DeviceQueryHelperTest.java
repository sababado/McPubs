package com.sababado.mcpubs.backend.db;

import com.sababado.ezdb.DbHelper;
import com.sababado.mcpubs.backend.models.Device;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by robert on 8/31/16.
 */
public class DeviceQueryHelperTest {
    Connection connection = null;

    @Before
    public void setup() {
        try {
            connection = DbHelper.openConnection(MyConnectionParams.getInstance());
        } catch (Exception e) {
            DbHelper.closeConnection(connection);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateTokenTest() {
        // Insert new record
        String newToken = "AAA567980ghjklr7689asasdasdaasasdasd";
        Device actual = DeviceQueryHelper.updateToken(connection, null, newToken);
        assertNotNull(actual);
        assertEquals(newToken, actual.getDeviceToken());
        assertTrue(actual.getKeepAlive() > 0);


        // Update first record
        String oldToken = newToken;
        long oldId = actual.getId();
        newToken = "AAAsdjojojiodsdiofjasiofjs";
        actual = DeviceQueryHelper.updateToken(connection, oldToken, newToken);
        assertNotNull(actual);
        assertEquals(newToken, actual.getDeviceToken());
        assertEquals(oldId, (long) actual.getId());
    }

    @Test
    public void deleteDeviceTest() {
        String token = "AAAHGHGHGHG";
        boolean actual = DeviceQueryHelper.deleteDevice(connection, token);
        assertFalse(actual);

        DeviceQueryHelper.updateToken(connection, null, token);
        actual = DeviceQueryHelper.deleteDevice(connection, token);
        assertTrue(actual);
    }

    @Test
    public void testKeepAliveDevice() {
        // test straight forward keep alive. updating timestamp
        String deviceToken = "AAA567980ghjklr7689";
        Device device = DeviceQueryHelper.updateToken(connection, null, deviceToken);
        long lastKeepAlive = device.getKeepAlive();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        assertTrue(DeviceQueryHelper.updateDeviceKeepAlive(connection, deviceToken));
        device = DeviceQueryHelper.getDevice(connection, deviceToken);
        assertTrue(device.getKeepAlive() > lastKeepAlive);

        // test attempting to keep alive device that isn't registered
        deviceToken = "AAA567980ghjklr7689asdfs";
        assertNull(DeviceQueryHelper.getDevice(connection, deviceToken));
        assertTrue(DeviceQueryHelper.updateDeviceKeepAlive(connection, deviceToken));
        assertNotNull(DeviceQueryHelper.getDevice(connection, deviceToken));
    }

    @After
    public void cleanup() {
        try {
            connection.prepareStatement("DELETE FROM Device WHERE " + Device.DEVICE_TOKEN + " LIKE 'AAA%';").execute();
        } catch (Exception e) {
            DbHelper.closeConnection(connection);
            throw new RuntimeException(e);
        }
        DbHelper.closeConnection(connection);
    }
}
