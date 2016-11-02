package com.sababado.mcpubs.backend.db;

import com.sababado.ezdb.DbHelper;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.models.Pub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testGetDevicesToSync() throws SQLException {
        String insertQuery = "INSERT INTO `mcPubsDev`.`Device`\n" +
                "(`id`,`deviceToken`,`canSync`)\n" +
                "VALUES\n" +
                "(1,\"AAAWUhPzh0teAzLZ9-RN\",0),\n" +
                "(2,\"AAAjrjrjrieieruieurieu\",1),\n" +
                "(3,\"AAAasofjsdofjsdf098908009\",1);";
        connection.prepareStatement(insertQuery).execute();

        List<Device> deviceList = DeviceQueryHelper.getDevicesToSync(connection);
        assertNotNull(deviceList);
        assertEquals(2, deviceList.size());
        assertTrue(2L == deviceList.get(0).getId());
        assertTrue(3L == deviceList.get(1).getId());
    }

    @Test
    public void testResetDeviceCanSyncFlag() throws SQLException {
        String insertQuery = "INSERT INTO `mcPubsDev`.`Device`\n" +
                "(`id`,`deviceToken`,`canSync`)\n" +
                "VALUES\n" +
                "(1,\"AAAWUhPzh0teLZ9-RN\",0),\n" +
                "(2,\"AAAjrjrjrieieuieurieu\",1),\n" +
                "(3,\"AAAasofjsdofjsdf98908009\",1);";
        connection.prepareStatement(insertQuery).execute();

        List<Device> deviceList = DeviceQueryHelper.getDevicesToSync(connection);
        DeviceQueryHelper.resetCanSyncFlag(connection, deviceList);

        deviceList = DeviceQueryHelper.getDevicesToSync(connection);
        assertEquals(0, deviceList.size());
    }

    @Test
    public void testUpdateDevicesCanSync() throws SQLException {
        String insertQuery = "INSERT INTO `mcPubsDev`.`Device` (`id`,`deviceToken`,`canSync`) VALUES (1,\"AAAAeYz0t-WUhPzh0teAzLZ9-RN\",0),(2,\"AAAAjrjrjrjrjeruieurieu\",0),(3,\"AAAAasofjs908009\",0);";
        connection.prepareStatement(insertQuery).execute();
        insertQuery = "INSERT INTO `mcPubsDev`.`Pub` (`id`,`fullCode`,`rootCode`,`code`,`version`,`isActive`,`pubType`) VALUES (289,'AAAA4200.43','AAAA4200',43,'B',true, 2005), (290,'AAAA4200.19','AAAA4200',19,'B',true, 2005), (291,'AAAA4500.10','AAAA4500',10,'A',false, 2005), (292,'AAAA4500.11','AAAA4500',11,'A',true, 2005), (293,'AAAA3500.99','AAAA3500',99,'D',true, 2005), (294,'AAAA3200.18','AAAA3200',18,'C',false, 2005), (295,'AAAAP3200.18','AAAAP3200',18,'C',true, 2006);";
        connection.prepareStatement(insertQuery).execute();
        insertQuery = "INSERT INTO `mcPubsDev`.`PubDevices` (`deviceId`,`pubId`) VALUES (1,289),(1,295),(2,295),(2,294),(3,291),(3,295),(3,289),(3,290);";
        connection.prepareStatement(insertQuery).execute();

        assertEquals(false, DeviceQueryHelper.getDevice(connection, "AAAAeYz0t-WUhPzh0teAzLZ9-RN").isCanSync());
        assertEquals(false, DeviceQueryHelper.getDevice(connection, "AAAAjrjrjrjrjeruieurieu").isCanSync());
        assertEquals(false, DeviceQueryHelper.getDevice(connection, "AAAAasofjs908009").isCanSync());

        List<Pub> pubs = new ArrayList<>();
        pubs.add(PubQueryHelper.getPubRecord(connection, 289L, null, true));
        pubs.add(PubQueryHelper.getPubRecord(connection, 290L, null, true));
        pubs.add(PubQueryHelper.getPubRecord(connection, 291L, null, false));
        DeviceQueryHelper.updateDeviceCanSync(connection, pubs);

        assertEquals(true, DeviceQueryHelper.getDevice(connection, "AAAAeYz0t-WUhPzh0teAzLZ9-RN").isCanSync());
        assertEquals(false, DeviceQueryHelper.getDevice(connection, "AAAAjrjrjrjrjeruieurieu").isCanSync());
        assertEquals(true, DeviceQueryHelper.getDevice(connection, "AAAAasofjs908009").isCanSync());
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
