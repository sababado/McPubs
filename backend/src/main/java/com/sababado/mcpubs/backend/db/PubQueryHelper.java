package com.sababado.mcpubs.backend.db;

import com.sababado.mcpubs.backend.db.utils.Column;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.db.utils.QueryHelper;
import com.sababado.mcpubs.backend.models.Pub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robert on 9/16/15.
 */
public class PubQueryHelper extends QueryHelper {
    private static final Logger _logger = Logger.getLogger(PubQueryHelper.class.getName());
    static String[] SHORT_PUB_QUERY_WHERE_COLUMNS = {Column.ID, Pub.FULL_CODE, Pub.IS_ACTIVE};

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
            statement.setString(6, pub.getTitle());
            statement.setString(7, pub.getReadableTitle());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Pub failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pub.setId(generatedKeys.getLong(1));
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

    public static Pub getPubRecord(Connection connection, Long id, String fullCode, boolean isActive) {
        Object[] values = {id, fullCode, isActive};
        String where = QueryHelper.buildRecordQuery(SHORT_PUB_QUERY_WHERE_COLUMNS, values, true);
        List<Pub> recordList = DbUtils.getList(connection, Pub.class, where);
        if (recordList != null && recordList.size() > 0) {
            return recordList.get(0);
        }

        return null;
    }
}
