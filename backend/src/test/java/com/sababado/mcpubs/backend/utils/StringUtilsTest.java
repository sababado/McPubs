package com.sababado.mcpubs.backend.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 8/25/16.
 */
public class StringUtilsTest {
    @Test
    public void testIsEmptyOrWhitespace() {
        assertEquals(true, StringUtils.isEmptyOrWhitespace(""));
        assertEquals(true, StringUtils.isEmptyOrWhitespace("    "));
        assertEquals(true, StringUtils.isEmptyOrWhitespace(null));
        assertEquals(false, StringUtils.isEmptyOrWhitespace("nope"));
    }
}
