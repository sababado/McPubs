package com.sababado.mcpubs.backend.models;

/**
 * Created by robert on 9/10/16.
 */
public class SubscribeResponse {
    public ErrorResponse[] results;

    public static class ErrorResponse {
        public String error;
    }
}
