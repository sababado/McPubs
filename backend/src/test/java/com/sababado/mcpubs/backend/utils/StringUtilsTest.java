package com.sababado.mcpubs.backend.utils;

import com.sababado.ezdb.DbRecord;

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
        assertEquals("'One','Two','Three'", actual);

        actual = StringUtils.toCsv(strList, false);
        assertEquals("One,Two,Three", actual);
    }

    @Test
    public void testIdToCsv() {
        List<DbRecord> list = new ArrayList<>();
        list.add(new TestDbRecord(1));
        list.add(new TestDbRecord(4));
        list.add(new TestDbRecord(2));

        String actual = StringUtils.idToCsv(list);
        assertEquals("1,4,2", actual);
    }

    public class TestDbRecord extends DbRecord {
        long id;

        public TestDbRecord(long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }
}
