package com.sababado.mcpubs.backend.db;

import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.db.utils.QueryHelper;
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
        List<PubDevices> pubDevicesList = DbUtils.getList(connection, PubDevices.class, true, where);
        if (pubDevicesList != null && pubDevicesList.size() > 0) {
            return pubDevicesList.get(0);
        }
        return null;
    }
}
