package com.sababado.mcpubs.backend.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 9/1/16.
 */
public class PubDevicesTest {
    @Test
    public void testGetInsertQuery() {
        String expected = "INSERT INTO PubDevices " +
                "(PubDevices.deviceId,PubDevices.pubId) " +
                "VALUES (?,?);";
        String actual = PubDevices.getInsertQuery();
        assertEquals(expected, actual);
    }
}
