package com.sababado.mcpubs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;

import com.sababado.mcpubs.models.Pub;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by robert on 8/29/16.
 */
public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public static final long DAY = 1000 * 60 * 60 * 24;
    public static final long WEEK = DAY * 7;

    public static final String SP_FIREBASE_PUSH = "firebasePush";

    public static final String LAST_KEEP_ALIVE = "last_keep_alive";
    public static final String DEVICE_TOKEN = "device_token";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LAST_KEEP_ALIVE, DEVICE_TOKEN})
    public @interface SpKey {
    }

    public static void setMetaData(Context context, @SpKey String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).apply();
    }

    public static String getMetaData(Context context, @SpKey String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, null);
    }

    public static void setMetaData(Context context, @SpKey String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(key, value).apply();
    }

    public static long getLongMetaData(Context context, @SpKey String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, 0L);
    }

    public static boolean isPastTimeWithinTime(long timeToCheck, long timePeriod) {
        long now = System.currentTimeMillis();
        return now - timeToCheck <= timePeriod;
    }

    public static String getDt(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SP_FIREBASE_PUSH, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Utils.DEVICE_TOKEN, null);
    }

    public static Pub findPubByServerId(List<Pub> pubsList, long serverId) {
        for (Pub pub : pubsList) {
            if (pub.getPubServerId() == serverId) {
                return pub;
            }
        }
        return null;
    }

    public static int sequentialSearch(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }
}
