package com.sababado.mcpubs.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.sababado.mcpubs.Utils;

import java.io.IOException;

public class KeepAliveService extends IntentService {
    private static final String TAG = KeepAliveService.class.getSimpleName();
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
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SP_FIREBASE_PUSH, MODE_PRIVATE);
        String deviceToken = sharedPreferences.getString(Utils.DEVICE_TOKEN, null);
        if (!TextUtils.isEmpty(deviceToken)) {
            try {
                NetworkUtils.getDeviceService(this).keepAlive(deviceToken).execute();
                Utils.setMetaData(this, Utils.LAST_KEEP_ALIVE, System.currentTimeMillis());
            } catch (IOException e) {
                Log.e(TAG, "Problem with keep-alive: " + e.getMessage());
            }
        }
    }
}
