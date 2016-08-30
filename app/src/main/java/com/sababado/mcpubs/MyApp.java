package com.sababado.mcpubs;

import android.app.Application;
import android.content.Context;

import com.sababado.ezprovider.EasyProvider;
import com.sababado.mcpubs.models.Pub;
import com.sababado.mcpubs.provider.DatabaseHelper;

/**
 * Created by robert on 2/28/16.
 */
public class MyApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        EasyProvider.init(this, DatabaseHelper.class, Pub.class);
    }
}
