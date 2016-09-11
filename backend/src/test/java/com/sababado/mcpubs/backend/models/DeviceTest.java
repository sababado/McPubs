package com.sababado.mcpubs.backend.models;

import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 9/1/16.
 */
public class DeviceTest {
    @Test
    public void testGetInsertStatement() {

        String expected = "INSERT INTO Device " +
                "(Device.deviceToken,Device.lastNotificationFail) " +
                "VALUES (?,?);";
        String actual = Device.getInsertQuery();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetUpdateStatement() {
        String expected = "UPDATE Device " +
                "SET Device.deviceToken=?,Device.lastNotificationFail=? " +
                "WHERE Device.id=?;";
        String actual = Device.getUpdateQuery();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetUpdateByDeviceTokenStatement() {
        String expected = "UPDATE Device " +
                "SET Device.deviceToken=? " +
                "WHERE Device.deviceToken=?;";
        String actual = Device.getUpdateByDeviceTokenQuery();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetUpdateKeepAliveStatement() {
        String expected = "UPDATE Device " +
                "SET Device.keepAlive=CURRENT_TIMESTAMP " +
                "WHERE Device.deviceToken=?;";
        String actual = Device.getUpdateKeepAliveByDeviceTokenQuery();
        System.out.println(expected);
        assertEquals(expected, actual);
    }
}
