package com.sababado.mcpubs.backend.cron;

import com.sababado.mcpubs.backend.db.PubDevicesQueryHelper;
import com.sababado.mcpubs.backend.db.utils.DbUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by robert on 8/27/16.
 */
public class PubCleanup extends HttpServlet {
    private static final Logger _logger = Logger.getLogger(PubDevicesQueryHelper.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        _logger.setLevel(Level.ALL);
        Connection connection = DbUtils.openConnection();
        int pubsRemoved = PubDevicesQueryHelper.cleanupUnwatchedPubs(connection);
        _logger.info("Cleaning up " + pubsRemoved + " pub(s).");
        DbUtils.closeConnection(connection);
    }
}
