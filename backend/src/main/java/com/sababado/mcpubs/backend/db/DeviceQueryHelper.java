package com.sababado.mcpubs.backend.db;

import com.sababado.ezdb.DbHelper;
import com.sababado.ezdb.QueryHelper;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by robert on 9/16/15.
 */
public class DeviceQueryHelper extends QueryHelper {
    private static final Logger _logger = Logger.getLogger(DeviceQueryHelper.class.getName());

    public static Device updateToken(Connection connection, String oldToken, String newToken) {
        boolean isInsert = StringUtils.isEmptyOrWhitespace(oldToken);
        String query = isInsert ? Device.getInsertQuery() : Device.getUpdateByDeviceTokenQuery();
        try {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newToken);
            if (isInsert) {
                statement.setLong(2, 0); // set lastNotificationFail timestamp
            } else {
                statement.setString(2, oldToken); // update where the old token.
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Device failed, no rows affected.");
            }
            return getDevice(connection, newToken);
        } catch (SQLException e) {
            _logger.severe("Couldn't insert or update device token: " + oldToken + " / " + newToken + "\n" + e.getMessage());
        }
        return null;
    }

    public static Device getDevice(Connection connection, String deviceToken) {
        Object values[] = {deviceToken};
        String where = QueryHelper.buildWhereQuery(new String[]{Device.DEVICE_TOKEN}, values, true);
        List<Device> deviceList = DbHelper.getList(connection, Device.class, where);
        if (deviceList != null && deviceList.size() > 0) {
            return deviceList.get(0);
        }
        return null;
    }

    public static boolean deleteDevice(Connection connection, String deviceToken) {
        try {
            String deleteQuery = QueryHelper.buildDeleteQuery(
                    Device.class,
                    new String[]{Device.DEVICE_TOKEN},
                    new Object[]{deviceToken});
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting Device failed, no rows affected.");
            }
            return true;
        } catch (SQLException e) {
            _logger.severe("Couldn't delete device: " + deviceToken + "\n" + e.getMessage());
            return false;
        }
    }

    public static boolean updateDeviceKeepAlive(Connection connection, String deviceToken) {
        try {
            String updateQuery = Device.getUpdateKeepAliveByDeviceTokenQuery();
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, deviceToken);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                _logger.warning("Attempting to keep-alive non registered device.");
                Device device = updateToken(connection, null, deviceToken);
                if (device == null) {
                    throw new SQLException("Updating Device failed, no rows affected.");
                }
            }
            return true;
        } catch (SQLException e) {
            _logger.severe("Couldn't keep alive device: " + deviceToken + "\n" + e.getMessage());
            return false;
        }
    }
}
