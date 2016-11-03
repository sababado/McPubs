package com.sababado.mcpubs.backend.cron;

import com.sababado.ezdb.DbHelper;
import com.sababado.mcpubs.backend.db.DeviceQueryHelper;
import com.sababado.mcpubs.backend.db.MyConnectionParams;
import com.sababado.mcpubs.backend.models.Device;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by robert on 11/2/16.
 */

public class DeviceSync extends HttpServlet {
    private static final Logger _logger = Logger.getLogger(DeviceSync.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO For future, more cost effective and efficient implementations, use Ofy and the datastore.
        // It is probably plenty big enough to accommodate this amount of data.
        Connection connection = DbHelper.openConnection(MyConnectionParams.getInstance());
        // get 1000 devices that need a sync
        List<Device> devices = DeviceQueryHelper.getDevicesToSync(connection);

        // TODO send notification

        // set devices to no longer need a sync
        DeviceQueryHelper.resetCanSyncFlag(connection, devices);
        DbHelper.closeConnection(connection);
    }
}
