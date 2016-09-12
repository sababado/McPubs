package com.sababado.mcpubs.network;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.BaseColumns;
import android.util.Log;

import com.sababado.ezprovider.Contracts;
import com.sababado.mcpubs.models.Constants;
import com.sababado.mcpubs.models.Pub;

import java.io.IOException;

public class PubService extends IntentService {
    private static final String TAG = PubService.class.getSimpleName();
    private static final String ACTION_SAVE = "com.sababado.mcpubs.action.save";
    private static final String ACTION_DELETE = "com.sababado.mcpubs.action.delete";

    private static final String ARG_PUB = "arg_pub";

    public PubService() {
        super("PubService");
    }

    public static void startActionSavePub(Context context, Pub pub) {
        Intent intent = new Intent(context, PubService.class);
        intent.setAction(ACTION_SAVE);
        intent.putExtra(ARG_PUB, pub);
        context.startService(intent);
    }

    public static void startActionDeletePub(Context context, Pub pub) {
        Intent intent = new Intent(context, PubService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(ARG_PUB, pub);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Pub pub = intent.getParcelableExtra(ARG_PUB);
            if (ACTION_SAVE.equals(action)) {
                handleActionSave(pub);
            } else if (ACTION_DELETE.equals(action)) {
                handleActionDelete(pub);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSave(Pub pub) {
        try {
            pub.setSaveStatus(Constants.SAVE_STATUS_SAVING);
            savePub(pub);
            com.sababado.mcpubs.backend.pub.model.Pub savedPub =
                    NetworkUtils.addDeviceTokenHeader(NetworkUtils.getPubService(this)
                            .addPub(pub.getTitle(), pub.getPubType()), this)
                            .execute();
            pub.copyFrom(savedPub);
            pub.setSaveStatus(Constants.SAVE_STATUS_SAVED);
        } catch (IOException e) {
            pub.setSaveStatus(Constants.SAVE_STATUS_FAILED);
            Log.v(TAG, "Failed to add/update pub: " + pub + "\n" + e.getMessage());
        }

        savePub(pub);
    }

    private void savePub(Pub pub) {
        ContentValues values = pub.toContentValues();
        Contracts.Contract contract = Contracts.getContract(Pub.class);
        getContentResolver().update(contract.CONTENT_URI, values,
                Contracts.Contract.ID_COLUMN_NAME + "=" + String.valueOf(pub.getId()),
                null);
    }

    private void handleActionDelete(Pub pub) {
        try {
            if (pub.getSaveStatus() != Constants.SAVE_STATUS_FAILED && pub.getPubServerId() != 0) {
                pub.setSaveStatus(Constants.SAVE_STATUS_DELETING);
                savePub(pub);
                NetworkUtils.addDeviceTokenHeader(NetworkUtils.getPubService(this)
                        .deletePub(pub.getPubServerId()), this)
                        .execute();
                pub.setSaveStatus(Constants.SAVE_STATUS_SAVED);
            }

            Contracts.Contract contract = Contracts.getContract(Pub.class);
            getContentResolver().delete(contract.CONTENT_URI,
                    BaseColumns._ID + " = ?", new String[]{String.valueOf(pub.getId())});
        } catch (IOException e) {
            Log.v(TAG, "Failed to delete pub from server: " + pub + "\n" + e.getMessage());
            pub.setSaveStatus(Constants.SAVE_STATUS_SAVED);
            savePub(pub);
        }
    }
}
