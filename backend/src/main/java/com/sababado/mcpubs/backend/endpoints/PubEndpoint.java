/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.sababado.mcpubs.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.sababado.mcpubs.backend.db.DeviceQueryHelper;
import com.sababado.mcpubs.backend.db.PubDevicesQueryHelper;
import com.sababado.mcpubs.backend.db.PubQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;
import com.sababado.mcpubs.backend.models.Device;
import com.sababado.mcpubs.backend.models.Pub;
import com.sababado.mcpubs.backend.models.PubDevices;
import com.sababado.mcpubs.backend.models.PubNotification;
import com.sababado.mcpubs.backend.utils.EndpointUtils;
import com.sababado.mcpubs.backend.utils.Messaging;
import com.sababado.mcpubs.backend.utils.UnrecognizedPubException;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

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
     * Add a new pub.
     *
     * @param pubTitle
     * @param pubType
     */
    public Pub addPub(HttpServletRequest req,
                      @Named("pubTitle") String pubTitle,
                      @Named("pubType") int pubType)
            throws UnauthorizedException, BadRequestException, ConflictException {
        Connection connection = DbUtils.openConnection();
        Pub pub = null;
        try {
            String deviceToken = EndpointUtils.getDeviceTokenFromHeader(req);

            Device device = DeviceQueryHelper.getDevice(connection, deviceToken);
            if (device == null) {
                // Device isn't registered so register it
                device = DeviceEndpoint.register(connection, null, deviceToken);

                if (device == null) {
                    throw new UnauthorizedException("The device is not registered.");
                }
            }

            try {
                pub = new Pub();
                pub.setPubType(pubType);
                pub.setTitle(pubTitle);
                pub.setActive(true);
                pub = PubQueryHelper.insertOrUpdateRecord(connection, pub);
            } catch (UnrecognizedPubException e) {
                throw new BadRequestException("Invalid pub name '" + pubTitle + "'");
            } catch (BadRequestException e) {
                pub = PubQueryHelper.getPubRecord(connection, null, pub.getFullCode(), null);
            }

            if (pub != null) {
                PubDevices pubDevices = PubDevicesQueryHelper.insertPubDevicesRecord(connection, device.getId(), pub.getId());
                if (pubDevices == null) {
                    throw new ConflictException("Attempting to save a duplicate record. Same pub and same device.");
                }
                Messaging.subscribeToTopic(deviceToken, pub.getFullCode());
            }
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw e;
        }
        DbUtils.closeConnection(connection);
        return pub;
    }

    public void deletePub(HttpServletRequest req, @Named("pubId") long pubId) throws UnauthorizedException {
        String deviceToken = EndpointUtils.getDeviceTokenFromHeader(req);

        Connection connection = DbUtils.openConnection();
        try {
            Pub pub = PubQueryHelper.getPubRecord(connection, pubId, null, null);
            if (pub != null) {
                boolean success = Messaging.unsubscribeFromTopic(deviceToken, pub.getFullCode());
                log.info("unsubscribe success: " + success);
            }
            boolean success = PubDevicesQueryHelper.deletePubDevicesRecord(connection, deviceToken, pubId);
            log.info("deleting PubDevice record success: " + success);
        } catch (Exception e) {
            DbUtils.closeConnection(connection);
            throw e;
        }

        DbUtils.closeConnection(connection);
    }
}
