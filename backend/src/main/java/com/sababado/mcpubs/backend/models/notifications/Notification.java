package com.sababado.mcpubs.backend.models.notifications;

/**
 * Created by robert on 11/3/16.
 */

public abstract class Notification {
    public boolean dry_run;

    public Notification() {
        dry_run = false;
    }
}
