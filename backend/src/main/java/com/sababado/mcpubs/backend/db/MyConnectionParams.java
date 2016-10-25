package com.sababado.mcpubs.backend.db;

import com.google.appengine.api.utils.SystemProperty;
import com.sababado.ezdb.DbHelper;
import com.sababado.mcpubs.backend.utils.StringUtils;

/**
 * Created by robert on 10/25/16.
 */

public class MyConnectionParams implements DbHelper.ConnectionParams {

    private static DbHelper.ConnectionParams connectionParams;

    public static DbHelper.ConnectionParams getInstance() {
        if (connectionParams == null) {
            connectionParams = new MyConnectionParams();
        }
        return connectionParams;
    }

    @Override
    public String getConnectionUrl() {
        try {
            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
                // Load the class that provides the new "jdbc:google:mysql://" prefix.
                Class.forName("com.mysql.jdbc.GoogleDriver");
                return StringUtils.PROD_DB;
            } else {
                // Local MySQL instance to use during development.
                Class.forName("com.mysql.jdbc.Driver");
                return StringUtils.DEV_DB;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
