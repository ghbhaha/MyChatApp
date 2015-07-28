package com.suda.mychatapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Suda on 2015/7/26.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 4;

    private static final String DBNAME = "message.db";

    private String sqlLastMsg = "CREATE TABLE IF NOT EXISTS " +
            "last_msg (conversation_id varchar(24) primary key, " +
            "lastTime long, userName varchar(20),lastMsg varchar(200), " +
            "nikeName varchar(20), iconUrl varchar(60))";

    private String sqlUpdateLastMsg = "alter table last_msg add unreadCount int DEFAULT 0";

    private String sqlUser = "CREATE TABLE IF NOT EXISTS " +
            "user (objId varchar(24) primary key, userName varchar(20), nikeName varchar(20), " +
            "iconUrl varchar(60), sign varchar(40)," +
            "tel varchar(16), email varchar(20), sex varchar(2), birthday long)";

    private String sqlFriend = "CREATE TABLE IF NOT EXISTS " +
            "friend (objId varchar(24) primary key, userName varchar(20))";


    public DbOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlLastMsg);
        db.execSQL(sqlUser);
        db.execSQL(sqlFriend);
        db.execSQL(sqlUpdateLastMsg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL(sqlUpdateLastMsg);
        }

        if (oldVersion < 3) {
            db.execSQL(sqlFriend);

        } else if (oldVersion < 2) {
            db.execSQL(sqlUser);
        }
    }

}