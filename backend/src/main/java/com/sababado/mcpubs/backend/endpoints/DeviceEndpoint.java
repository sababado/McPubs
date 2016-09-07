/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.sababado.mcpubs.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.sababado.mcpubs.backend.db.DeviceQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.inject.Named;

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
     * @param newToken New token that is going to be used.
     */
    public void register(@Named("oldToken") String oldToken, @Named("newToken") String newToken) {
        Connection connection = DbUtils.openConnection();

        register(connection, oldToken, newToken);

        DbUtils.closeConnection(connection);
    }

    static Device register(Connection connection, String oldToken, String newToken) {
        Device device = DeviceQueryHelper.updateToken(connection, oldToken, newToken);
        // TODO Can I register the device with firebase here?

        return device;
    }

    /**
     * Remind the server that this device is still in use.
     *
     * @param deviceToken Device to keep alive.
     */
    public void keepAlive(@Named("deviceToken") String deviceToken) {
        Connection connection = DbUtils.openConnection();

        DeviceQueryHelper.updateDeviceKeepAlive(connection, deviceToken);

        DbUtils.closeConnection(connection);
    }
}
