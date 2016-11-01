package com.sababado.mcpubs.backend.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testToCsv() {
        List<String> strList = new ArrayList<>();
        strList.add("One");
        strList.add("Two");
        strList.add("Three");

        String actual = StringUtils.toCsv(strList, true);
        assertEquals("'One','Two','Three'",actual);

        actual = StringUtils.toCsv(strList, false);
        assertEquals("One,Two,Three",actual);
    }
}
