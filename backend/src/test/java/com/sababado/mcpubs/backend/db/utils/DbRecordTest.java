package com.sababado.mcpubs.backend.db.utils;

import com.sababado.mcpubs.backend.models.Pub;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by robert on 8/25/16.
 */
public class DbRecordTest {

    @Test
    public void testGetInsertStatement() {

        DbRecord.insertStatements.clear();
        assertEquals(0, DbRecord.insertStatements.size());

        String expected = "INSERT INTO Pub " +
                "(Pub.fullCode,Pub.rootCode,Pub.code,Pub.version,Pub.isActive,Pub.lastUpdated,Pub.title,Pub.readableTitle) " +
                "VALUES (?,?,?,?,?,?,?,?);";
        String actual = Pub.getInsertQuery();
        assertEquals(expected, actual);

        assertEquals(1, DbRecord.insertStatements.size());
    }
}
