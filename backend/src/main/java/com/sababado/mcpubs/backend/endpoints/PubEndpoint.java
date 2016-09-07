/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.sababado.mcpubs.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.sababado.mcpubs.backend.db.DeviceQueryHelper;
import com.sababado.mcpubs.backend.db.PubDevicesQueryHelper;
import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.models.PubDevices;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint to handle CRUD operations for Pubs
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
        name = "pub",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.mcpubs.sababado.com",
                ownerName = "backend.mcpubs.sababado.com",
                packagePath = ""
        )
)
public class PubEndpoint {
    private static final Logger log = Logger.getLogger(PubEndpoint.class.getName());

    /**
     * Add a new pub or edit an existing pub by passing a valid pubId.
     *
     * @param pubTitle
     * @param pubId
     */
    public void addPub(@Named("pubId") long pubId,
                       @Named("pubTitle") String pubTitle,
                       @Named("pubType") int pubType) {
        // TODO get deviceToken from header?
        String deviceToken = "";

        Connection connection = DbUtils.openConnection();

        Device device = DeviceQueryHelper.getDevice(connection, deviceToken);
        if (device == null) {
            // Device isn't registered so register it
            device = DeviceEndpoint.register(connection, null, deviceToken);

            if (device == null) {
                // TODO return an error, device not registered.
            }
        }

        Pub pub = null;
        try {
            pub = new Pub();
            pub.setId(pubId);
            pub.setTitle(pubTitle);
            pub.setPubType(pubType);
            pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);
        } catch (UnrecognizedPubException e) {
            // TODO return an error, invalid pub name
        }

        if (device != null && pub != null) {
            PubDevices pubDevices = PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
            if (pubDevices == null) {
                // TODO Return a warning, attempting to save a duplicate record (same pub and same device)
            }
            // TODO success
        }
        DbUtils.closeConnection(connection);
    }

    public void deletePub(@Named("pubId") long pubId) {
        // TODO deviceToken from header?
        String deviceToken = "";
        Connection connection = DbUtils.openConnection();

        PubDevicesQueryHelper.deletePubDevicesRecord(connection, deviceToken, pubId);

        DbUtils.closeConnection(connection);
    }
}
