package com.sababado.mcpubs.backend.utils;

import com.sababado.mcpubs.backend.utils.PubUtils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by robert on 8/27/16.
 */
public class PubUtilsTest {

    @Test
    public void testParseStatus() {
        assertTrue(PubUtils.parseStatus("New"));
        assertTrue(PubUtils.parseStatus("Current"));
        assertFalse(PubUtils.parseStatus("Deleted"));
    }
}
