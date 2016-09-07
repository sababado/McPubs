package com.sababado.mcpubs;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class KeepAliveService extends IntentService {
    private static final String ACTION_KEEP_ALIVE = "com.sababado.mcpubs.action.FOO";

    public KeepAliveService() {
        super("KeepAliveService");
    }

    public static void startActionKeepAlive(Context context) {
        Intent intent = new Intent(context, KeepAliveService.class);
        intent.setAction(ACTION_KEEP_ALIVE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_KEEP_ALIVE.equals(action)) {
                handleActionKeepAlive();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionKeepAlive() {
        // TODO API Call
        throw new UnsupportedOperationException("Not yet implemented");
//        Utils.setMetaData(this, Utils.LAST_KEEP_ALIVE, System.currentTimeMillis());
    }
}
