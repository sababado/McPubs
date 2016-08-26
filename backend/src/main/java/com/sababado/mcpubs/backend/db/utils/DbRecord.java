package com.sababado.mcpubs.backend.db.utils;

import com.sababado.mcpubs.backend.utils.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 9/15/15.
 */
public abstract class DbRecord {
    static Map<Class<? extends DbRecord>, String> insertStatements = new HashMap<>(3);

    public DbRecord() {
    }

    /**
     * Initializes the record with the result set's current position.
     *
     * @param resultSet Result set on the position to initialize from.
     */
    public DbRecord(ResultSet resultSet) throws SQLException {

    }

    public abstract Long getId();

    public abstract void setId(Long id);

    protected static String getInsertQuery(Class<? extends DbRecord> cls, boolean isFk) {
        String statement = insertStatements.get(cls);
        if (statement != null) {
            return statement;
        }

        String tableName = DbUtils.getTableName(cls).value();
        String fields = DbUtils.getSelectColumns(cls, isFk, tableName).replace("Pub.id,", "");
        int numFields = fields.split(",").length;
        statement = "INSERT INTO " + tableName +
                " (" + fields + ") " +
                "VALUES (" + StringUtils.generateQuestionString(numFields) + ");";

        insertStatements.put(cls, statement);
        return statement;
    }
}
