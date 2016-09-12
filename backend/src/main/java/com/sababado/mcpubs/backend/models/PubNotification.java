package com.sababado.mcpubs.backend.models;

/**
 * Created by robert on 9/12/16.
 */
public class PubNotification {
    Pub data;
    String to;

    public PubNotification(Pub data, String to, boolean isTopic) {
        this.data = data;
        if (isTopic && !to.startsWith("/topic/")) {
            this.to = "/topic/" + to;
        } else {
            this.to = to;
        }
    }
}
