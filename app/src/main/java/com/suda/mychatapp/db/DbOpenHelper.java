package com.suda.mychatapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Suda on 2015/7/26.
 */
public class DbOpenHelper extends SQLiteOpenHelper {


    private static final String DBNAME = "message.db";

    private String sqlLastMsg = "CREATE TABLE IF NOT EXISTS " +
            "last_msg (conversation_id varchar(24) primary key, lastTime long, userName varchar(20),lastMsg varchar(200), nikeName varchar(20), iconUrl varchar(60))";

    private static final int VERSION = 1;

    public DbOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlLastMsg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

}