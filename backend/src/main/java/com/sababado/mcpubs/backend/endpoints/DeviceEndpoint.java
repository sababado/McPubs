/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.sababado.mcpubs.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

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
    public void registerDevice(@Named("oldToken") String oldToken, @Named("newToken") String newToken) {
        // TODO Can I register the device with firebase here?

    }
}
