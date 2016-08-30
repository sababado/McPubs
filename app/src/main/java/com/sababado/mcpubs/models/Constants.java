package com.sababado.mcpubs.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by robert on 8/29/16.
 */
public class Constants {
    public static final int NO_CHANGE = 0;
    public static final int UPDATED = 1;
    public static final int UPDATED_BUT_DELETED = 2;
    public static final int DELETED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_CHANGE, UPDATED, UPDATED_BUT_DELETED, DELETED})
    public @interface UpdateStatus {
    }
}
