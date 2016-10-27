package com.sababado.mcpubs.backend.db;

import com.google.api.server.spi.response.BadRequestException;
import com.sababado.ezdb.Column;
import com.sababado.ezdb.DbHelper;
import com.sababado.ezdb.QueryHelper;
import com.sababado.mcpubs.backend.models.Pub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robert on 9/16/15.
 */
public class PubQueryHelper extends QueryHelper {
    private static final Logger _logger = Logger.getLogger(PubQueryHelper.class.getName());
    public static final String ACTIVE_PUB_WHERE_CLAUSE = Pub.IS_ACTIVE + "=true ";

    static String[] SHORT_PUB_QUERY_WHERE_COLUMNS = {Column.ID, Pub.FULL_CODE, Pub.IS_ACTIVE};

    public static void batchUpdate(Connection connection, List<Pub> records) {
        try {
            PreparedStatement statement = connection.prepareStatement(Pub.getUpdateQuery());

            for (Pub pub : records) {
                statement.setString(1, pub.getFullCode());
                statement.setString(2, pub.getRootCode());
                statement.setInt(3, pub.getCode());
                statement.setString(4, pub.getVersion());
                statement.setBoolean(5, pub.isActive());
                statement.setInt(6, pub.getPubType());
                statement.setString(7, pub.getTitle());
                statement.setString(8, pub.getReadableTitle());
                statement.setLong(9, pub.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Pub insertOrUpdateRecord(Connection connection, Pub pub) throws BadRequestException {
        Pub pubRecord = getPubRecord(connection, pub.getId(), null, null);
        boolean isInsert = pubRecord == null;
        String query = isInsert ? Pub.getInsertQuery() : Pub.getUpdateQuery();
        try {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pub.getFullCode());
            statement.setString(2, pub.getRootCode());
            statement.setInt(3, pub.getCode());
            statement.setString(4, pub.getVersion());
            statement.setBoolean(5, pub.isActive());
            statement.setInt(6, pub.getPubType());
            statement.setString(7, pub.getTitle());
            statement.setString(8, pub.getReadableTitle());
            if (!isInsert) {
                statement.setLong(9, pub.getId());
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Pub failed, no rows affected.");
            }
            if (!isInsert && affectedRows == 1) {
                // Update successful
                return getPubRecord(connection, pub.getId(), null, null);
            }

            // return newly inserted record.
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pub = getPubRecord(connection, generatedKeys.getLong(1), null, null);
                    statement.close();
                    return pub;
                } else {
                    statement.close();
                    throw new SQLException("Creating Pub failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            String message = "Couldn't insert or update Pub: " + pub.getFullCode() + "\n" + e.getMessage();
            _logger.severe(message);
            throw new BadRequestException(message);
        }
    }

    public static Pub insertRecordIfNonExistent(Connection connection, Pub pub) {
        Pub pubRecord = getPubRecord(connection, pub.getId(), pub.getFullCode(), pub.isActive());
        if (pubRecord != null) {
            return pubRecord;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(Pub.getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pub.getFullCode());
            statement.setString(2, pub.getRootCode());
            statement.setInt(3, pub.getCode());
            statement.setString(4, pub.getVersion());
            statement.setBoolean(5, pub.isActive());
            statement.setInt(6, pub.getPubType());
            statement.setString(7, pub.getTitle());
            statement.setString(8, pub.getReadableTitle());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Pub failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pub = getPubRecord(connection, generatedKeys.getLong(1), null, null);
                    statement.close();
                    return pub;
                } else {
                    statement.close();
                    throw new SQLException("Creating Pub failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            _logger.severe("Couldn't insert new Pub: " + pub.getFullCode() + "\n" + e.getMessage());
        }
        return null;
    }

    public static Pub getPubRecord(Connection connection, Long id, String fullCode, Boolean isActive) {
        Object[] values = {id, fullCode, isActive};
        String where = QueryHelper.buildWhereQuery(SHORT_PUB_QUERY_WHERE_COLUMNS, values, true);
        List<Pub> recordList = DbHelper.getList(connection, Pub.class, where);
        if (recordList != null && recordList.size() > 0) {
            return recordList.get(0);
        }

        return null;
    }

    public static List<String> getDistinctRootCodes(Connection connection, int pubType, String where) {
        where = where == null ? "" : "and " + where;
        where = "where " + ACTIVE_PUB_WHERE_CLAUSE +
                " and " + Pub.PUB_TYPE + "=" + pubType + " " +
                where;

        List<Object> distinctValues = DbHelper.getDistinctList(connection, Pub.class,
                Pub.ROOT_CODE,
                where);

        if (distinctValues == null) {
            return null;
        }
        return (ArrayList<String>) (ArrayList<?>) distinctValues;
    }

    public static boolean deletePub(Connection connection, long pubId) {
        try {
            String deleteQuery = QueryHelper.buildDeleteQuery(
                    Pub.class,
                    new String[]{Pub.ID},
                    new Object[]{pubId});
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting Pub failed, no rows affected.");
            }
            return true;
        } catch (SQLException e) {
            _logger.severe("Couldn't delete Pub: " + pubId + "\n" + e.getMessage());
            return false;
        }
    }
}
