package com.sababado.mcpubs.backend.db;

import com.sababado.ezdb.DbHelper;
import com.sababado.ezdb.QueryHelper;
import com.sababado.mcpubs.backend.models.PubDevices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robert on 9/1/16.
 */
public class PubDevicesQueryHelper extends QueryHelper {
    private static final Logger _logger = Logger.getLogger(PubDevicesQueryHelper.class.getName());

    public static PubDevices insertPubDevicesRecord(Connection connection, long deviceId, long pubId) {

        try {
            PreparedStatement statement = connection.prepareStatement(PubDevices.getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, deviceId);
            statement.setLong(2, pubId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating PubDevices failed, no rows affected.");
            }

            // return newly inserted record.
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    PubDevices pubDevices = getPubDevicesRecord(connection, generatedKeys.getLong(1));
                    statement.close();
                    return pubDevices;
                } else {
                    statement.close();
                    throw new SQLException("Creating Pub failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            _logger.severe("Couldn't insert pubdevices record --- deviceId:" + deviceId + " pubId: " + pubId + "\n" + e.getMessage());
        }
        return null;
    }

    public static PubDevices getPubDevicesRecord(Connection connection, long id) {
        String where = QueryHelper.buildWhereQuery(new String[]{PubDevices.PUB_DEVICES_ID}, new Object[]{id}, true);
        List<PubDevices> pubDevicesList = DbHelper.getList(connection, PubDevices.class, true, where);
        if (pubDevicesList != null && pubDevicesList.size() > 0) {
            return pubDevicesList.get(0);
        }
        return null;
    }

    public static boolean deletePubDevicesRecord(Connection connection, String deviceToken, long pubId) {
        try {
            String deleteQuery = "delete from PubDevices " +
                    "where pubId = " + pubId + " " +
                    "and deviceId = (select id from Device where deviceToken='" + deviceToken + "');";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting PubDevices Record failed, no rows affected.");
            }
            return true;
        } catch (SQLException e) {
            _logger.severe("Couldn't delete PubDevices Record: deviceToken:" + deviceToken + " pubId:" + pubId + "\n" + e.getMessage());
            return false;
        }
    }

    public static int cleanupUnwatchedPubs(Connection connection) {
        int affectedRows = 0;
        try {
            String query = "delete from Pub " +
                    "where Pub.id not in " +
                    "(select PubDevices.pubId from PubDevices group by(PubDevices.pubId));";
            PreparedStatement statement = connection.prepareStatement(query);
            affectedRows = statement.executeUpdate();
        } catch (SQLException e) {
            _logger.severe("Couldn't cleanup unwatched pubs.\n" + e.getMessage());
        }
        return affectedRows;
    }
}
