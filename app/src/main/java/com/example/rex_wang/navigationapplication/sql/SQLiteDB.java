package com.example.rex_wang.navigationapplication.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Rex_Wang on 2017/3/31.
 */

public class SQLiteDB extends SQLiteOpenHelper {
    private final static int mDBVersion = 1; //<-- 版本
    private final static String mDBName = "BeaconListdb";  //<-- db name
    private final static String mTableName = "BeaconTable"; //<-- table name
    public SQLiteDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public SQLiteDB(Context context) {
        super(context, mDBName, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + mTableName + "(" +
                "_id INTEGER PRIMARY KEY  NOT NULL , " +
                "address VARCHAR(50), " +
                "Name VARCHAR(50), " +
                "Rssi VARCHAR(50), " +
                "Distance VARCHAR(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS" + mTableName);
    }
}
