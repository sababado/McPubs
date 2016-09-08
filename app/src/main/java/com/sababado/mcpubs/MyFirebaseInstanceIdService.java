package com.sababado.mcpubs;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sababado.mcpubs.network.NetworkUtils;

import java.io.IOException;

/**
 * Created by robert on 8/31/16.
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String newToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SP_FIREBASE_PUSH, MODE_PRIVATE);
        String oldToken = sharedPreferences.getString(Utils.DEVICE_TOKEN, null);
        sharedPreferences.edit()
                .putString(Utils.DEVICE_TOKEN, newToken)
                .apply();
        Log.d(TAG, "registering device: " + oldToken + " / " + newToken);
        if (oldToken == null) {
            oldToken = "";
        }
        try {
            NetworkUtils.getDeviceService(this).register(oldToken, newToken).execute();
            Utils.setMetaData(this, Utils.LAST_KEEP_ALIVE, System.currentTimeMillis());
        } catch (IOException e) {
            Log.e(TAG, "Problem with device registration: " + e.getMessage());
        }
    }
}
