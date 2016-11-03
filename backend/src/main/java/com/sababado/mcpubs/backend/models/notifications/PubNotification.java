package com.sababado.mcpubs.backend.models.notifications;

import com.sababado.mcpubs.backend.models.Pub;

/**
 * Created by robert on 9/12/16.
 */
public class PubNotification extends Notification {
    Pub data;
    String to;

    public PubNotification(Pub data, String to, boolean isTopic) {
        super();
        this.data = data;
        if (isTopic && !to.startsWith("/topics/")) {
            this.to = "/topics/" + to;
        } else {
            this.to = to;
        }
    }
}
