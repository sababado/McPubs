/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.sababado.mcpubs.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.UnauthorizedException;
import com.sababado.mcpubs.backend.db.DeviceQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.utils.EndpointUtils;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * Endpoint to handle device registration
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
        name = "device",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.mcpubs.sababado.com",
                ownerName = "backend.mcpubs.sababado.com",
                packagePath = ""
        )
)
public class DeviceEndpoint {
    private static final Logger log = Logger.getLogger(DeviceEndpoint.class.getName());

    /**
     * Register a device with it's newest device token. This token is used for notifications.
     *
     * @param oldToken Old token that was being used.
     */
    public void register(HttpServletRequest req, @Named("oldToken") String oldToken) throws UnauthorizedException {
        Connection connection = DbUtils.openConnection();

        try {
            String deviceToken = EndpointUtils.getDeviceTokenFromHeader(req);
            register(connection, oldToken, deviceToken);
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw e;
        }
        DbUtils.closeConnection(connection);
    }

    static Device register(Connection connection, String oldToken, String newToken) {
        Device device = DeviceQueryHelper.updateToken(connection, oldToken, newToken);
        return device;
    }

    /**
     * Remind the server that this device is still in use.
     */
    public void keepAlive(HttpServletRequest req) throws UnauthorizedException {
        Connection connection = DbUtils.openConnection();

        try {
            String deviceToken = EndpointUtils.getDeviceTokenFromHeader(req);
            DeviceQueryHelper.updateDeviceKeepAlive(connection, deviceToken);
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw e;
        }
        DbUtils.closeConnection(connection);
    }
}
