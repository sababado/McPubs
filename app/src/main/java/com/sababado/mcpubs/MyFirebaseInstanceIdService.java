package com.sababado.mcpubs;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
        SharedPreferences sharedPreferences = getSharedPreferences("firebasePush", MODE_PRIVATE);
        String oldToken = sharedPreferences.getString("token", null);
        sharedPreferences.edit()
                .putString("token", newToken)
                .apply();
        // TODO API call send oldToken and newToken
    }
}
