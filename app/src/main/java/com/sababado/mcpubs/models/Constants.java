package com.sababado.mcpubs.models;

import android.content.Context;
import android.support.annotation.IntDef;

import com.sababado.mcpubs.R;

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

    public static final int MCO = 2005;
    public static final int MCO_P = 2006;
    public static final int NAVMC = 2008;
    public static final int NAVMC_DIR = 2009;
    public static String[] PUB_TYPES;

    public static void init(Context context) {
        PUB_TYPES = context.getResources().getStringArray(R.array.pub_types);
    }

    public static final int[] PUB_TYPE_VALS = {MCO, MCO_P, NAVMC, NAVMC_DIR};

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MCO, MCO_P, NAVMC, NAVMC_DIR})
    public @interface PubType {
    }
}
