package com.sababado.mcpubs.backend.models;

import com.google.appengine.repackaged.com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by robert on 9/11/16.
 */
public class SubscribeResponseTest {

    @Test
    public void testParse() {
        String str = "{\"results\":[{}]}";

        SubscribeResponse results = new Gson().fromJson(str, SubscribeResponse.class);
        assertNotNull(results);
        assertNotNull(results.results);
    }
}
