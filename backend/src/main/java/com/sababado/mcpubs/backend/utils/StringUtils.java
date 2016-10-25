package com.sababado.mcpubs.backend.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by robert on 9/16/15.
 */
public class StringUtils {

    public static final SimpleDateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);

    private static final String PROD_PROJECT_ID = "voltaic-flag-141523"; // TODO Replace with APP_ENGINE_ID
    private static final String PROD_DB_INSTANCE = "mcpubs-db2"; // TODO Replace with Prod DB instance name
    private static final String PROD_DB_NAME = "MCPUBS"; // TODO Replace with Prod DB name
    private static final String PROD_DB_USER = "user=root"; // TODO Replace with PROD user connection info.
    public static final String PROD_DB = "jdbc:google:mysql://" +
            PROD_PROJECT_ID + ":" + PROD_DB_INSTANCE + "/" + PROD_DB_NAME + "?" + PROD_DB_USER + "&zeroDateTimeBehavior=convertToNull";
    public static final String DEV_DB = "jdbc:mysql://127.0.0.1:8889/mcPubsDev?user=root&zeroDateTimeBehavior=convertToNull";
    public static final String HEADER_DEVICE_TOKEN = "dT";
    /**
     * Use this regex to find one of any letter.
     */
    public static final String REGEX_ANY_LETTER = "[a-zA-Z]";

    public static boolean isEmptyOrWhitespace(String string) {
        return string == null || string.trim().equals("");
    }
}
