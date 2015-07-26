package com.suda.mychatapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.suda.mychatapp.Conf;
import com.suda.mychatapp.db.pojo.LastMessage;

import java.util.ArrayList;

/**
 * Created by Suda on 2015/7/26.
 */
public class DbHelper {

    private DbOpenHelper dbOpenHelper;

    public DbHelper(Context context) {
        // TODO Auto-generated constructor stub
        this.dbOpenHelper = new DbOpenHelper(context);
    }

    public void clearAllData(){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM last_msg");
    }

    public void addLastMess(LastMessage message) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {

            if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversation_id())) {
                db.execSQL(
                        "insert into last_msg(conversation_id, lastTime, userName, nikeName,iconUrl,lastMsg) values(?,?,?,?,?,?)",
                        new Object[]{message.getConversation_id(), message.getLastTime(),
                                message.getUserName(),"群聊:"+ message.getNikeName(), message.getIconUrl(), message.getLastMsg()});
            } else {
                db.execSQL(
                        "insert into last_msg(conversation_id, lastTime, userName, nikeName,iconUrl,lastMsg) values(?,?,?,?,?,?)",
                        new Object[]{message.getConversation_id(), message.getLastTime(),
                                message.getUserName(), message.getNikeName(), message.getIconUrl(), message.getLastMsg()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteLastMsgById(String conversationId){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM last_msg where conversation_id='"+conversationId+"'");
    }

    public void updateLastMsg(LastMessage message) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("lastTime", String.valueOf(message.getLastTime()));
            if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversation_id())) {
                values.put("nikeName", "群聊:" + message.getUserName());
                values.put("iconUrl", message.getIconUrl());
            }
            values.put("lastMsg", message.getLastMsg());
            db.update("last_msg", values, "conversation_id=?", new String[]{message.getConversation_id()});
        } catch (SQLException e) {//异常处理
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    public boolean isExistMsg(String conversationid) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("last_msg", null, "conversation_id=?",
                new String[]{conversationid}, null, null, null);
        boolean tmp = cursor.moveToFirst();
        db.close();
        return tmp;
    }

    public ArrayList<LastMessage> findLastMsg() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("last_msg", null, null,
                null, null, null, null);

        if (!cursor.moveToFirst()) {
            db.close();
            return null;
        }

        ArrayList<LastMessage> arrayList = new ArrayList<LastMessage>();

        for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {

            arrayList.add(new LastMessage(cursor.getString(cursor.getColumnIndex("conversation_id")), cursor.getString(cursor.getColumnIndex("userName"))
                    , cursor.getString(cursor.getColumnIndex("nikeName")), cursor.getString(cursor.getColumnIndex("iconUrl"))
                    , cursor.getLong(cursor.getColumnIndex("lastTime")), cursor.getString(cursor.getColumnIndex("lastMsg"))));
        }
        db.close();
        return arrayList;
    }

}
