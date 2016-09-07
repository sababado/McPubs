package com.sababado.mcpubs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sababado.mcpubs.models.Constants;
import com.sababado.mcpubs.models.Constants.UpdateStatus;
import com.sababado.mcpubs.ui.MyPubsActivity;

/**
 * Created by robert on 8/31/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // TODO generate notification
    }

    private void showNotification(String oldTitle, String newTitle, @UpdateStatus int pubType) {
        String title = "";
        String message = "";
        switch (pubType) {
            case Constants.DELETED:
                title = getString(R.string.notif_message_deleted, oldTitle);
                message = getString(R.string.notification_message_deleted, oldTitle);
                break;
            case Constants.UPDATED:
                title = getString(R.string.notif_message_udpated, oldTitle);
                message = getString(R.string.notification_message_updated, oldTitle, newTitle);
                break;
            case Constants.UPDATED_BUT_DELETED:
                title = getString(R.string.notif_message_updated_but_deleted, oldTitle);
                message = getString(R.string.notification_message_updated_but_deleted, oldTitle, newTitle);
                break;
        }

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setTicker(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MyPubsActivity.class);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        // Once parse's sdk stops sending a notification we'll send one.
        mNotifyMgr.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
