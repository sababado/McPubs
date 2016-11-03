package com.sababado.mcpubs.backend.models.notifications;

/**
 * Created by robert on 9/10/16.
 */
public class FcmResponse {
    public ErrorResponse[] results;

    public static class ErrorResponse {
        public String error;
    }
}
