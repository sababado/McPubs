package com.sababado.mcpubs.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sababado.ezprovider.Contracts;
import com.sababado.mcpubs.models.Pub;

/**
 * Database helper.
 * Created by robert on 3/1/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "mcpubs.db";
    public static final int VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contracts.getContract(Pub.class).SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.getContract(Pub.class).TABLE_NAME);
        onCreate(db);
    }
}
