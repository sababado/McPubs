package com.sababado.mcpubs.backend.utils;

import com.google.api.server.spi.response.UnauthorizedException;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by robert on 9/8/16.
 */
public class EndpointUtils {
    private static final Logger log = Logger.getLogger(EndpointUtils.class.getName());

    public static String getDeviceTokenFromHeader(HttpServletRequest req) throws UnauthorizedException {
        String deviceToken = req.getHeader(StringUtils.HEADER_DEVICE_TOKEN);
        if (StringUtils.isEmptyOrWhitespace(deviceToken)) {
            String msg = "dT doesn't exist in header.";
            log.severe(msg);

            throw new UnauthorizedException(msg);
        }
        return deviceToken;
    }
}
