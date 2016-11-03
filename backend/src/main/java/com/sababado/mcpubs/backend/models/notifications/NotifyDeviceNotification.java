package com.sababado.mcpubs.backend.models.notifications;

import com.sababado.mcpubs.backend.models.Device;

import java.util.List;

/**
 * Created by robert on 11/2/16.
 */

public class NotifyDeviceNotification extends Notification {
    final private String data = "{\"sync\":true}";
    final private String[] registration_ids;

    public NotifyDeviceNotification(List<Device> devices) {
        super();
        registration_ids = new String[devices.size()];
        for (int i = 0; i < registration_ids.length; i++) {
            registration_ids[i] = devices.get(i).getDeviceToken();
        }
        dry_run = false;
    }
}
