package com.sababado.mcpubs.backend.utils;

/**
 * Created by robert on 8/27/16.
 */
public class PubUtils {
    public static final String PS_NEW = "New";
    public static final String PS_CURRENT = "Current";
    public static final String PS_DELETED = "Deleted";

    public static boolean parseStatus(String status) {
        return PS_CURRENT.equalsIgnoreCase(status) || PS_NEW.equalsIgnoreCase(status);
    }
}
