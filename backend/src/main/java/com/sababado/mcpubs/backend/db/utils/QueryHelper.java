package com.sababado.mcpubs.backend.db.utils;

import java.util.logging.Logger;

/**
 * Created by robert on 9/16/15.
 */
public abstract class QueryHelper {
    protected static final Logger _logger = Logger.getLogger(DbUtils.class.getName());

    static String appendValue(String where, String columnName, DbRecord dbRecord) {
        if (dbRecord != null) {
            where = where == null ? "where" : where + " and";
            where += String.format(" %s = %d", columnName, dbRecord.getId());
        }
        return where;
    }

    static String appendValue(String where, String columnName, String columnValue) {
        if (columnValue != null) {
            where = where == null ? "where" : where + " and";
            where += String.format(" %s = '%s'", columnName, columnValue);
        }
        return where;
    }

    static String appendValue(String where, String columnName, Long columnValue) {
        if (columnValue != null) {
            where = where == null ? "where" : where + " and";
            where += String.format(" %s = %d", columnName, columnValue);
        }
        return where;
    }

    static String appendValue(String where, String columnName, Integer columnValue) {
        if (columnValue != null) {
            where = where == null ? "where" : where + " and";
            where += String.format(" %s = %d", columnName, columnValue);
        }
        return where;
    }

    static String limitOne(String where) {
        if (where != null) {
            where += " limit 1";
        }
        return where;
    }
}
