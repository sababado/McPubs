package com.sababado.mcpubs.backend.models;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.sababado.mcpubs.backend.models.notifications.FcmResponse;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by robert on 9/11/16.
 */
public class FcmResponseTest {

    @Test
    public void testParse() {
        String str = "{\"results\":[{}]}";

        FcmResponse results = new Gson().fromJson(str, FcmResponse.class);
        assertNotNull(results);
        assertNotNull(results.results);
    }
}
