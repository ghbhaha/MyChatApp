package com.suda.mychatapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.suda.mychatapp.Conf;
import com.suda.mychatapp.db.pojo.LastMessage;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.DateFmUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Suda on 2015/7/26.
 */
public class DbHelper {

    private DbOpenHelper dbOpenHelper;

    public DbHelper(Context context) {
        // TODO Auto-generated constructor stub
        this.dbOpenHelper = new DbOpenHelper(context);
    }

    public void clearAllData() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM last_msg");
        db.execSQL("DELETE FROM user");
        db.execSQL("DELETE FROM friend");
    }

    public void addLastMess(LastMessage message) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {

            if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversation_id())) {
                db.execSQL(
                        "insert into last_msg(conversation_id, lastTime, userName, nikeName,iconUrl,lastMsg) values(?,?,?,?,?,?)",
                        new Object[]{message.getConversation_id(), message.getLastTime(),
                                message.getUserName(), "群聊:" + message.getNikeName(), message.getIconUrl(), message.getLastMsg()});
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

    public void addUser(User user) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {
            db.execSQL(
                    "insert into user(objId, userName, nikeName, sign, iconUrl, tel, email, sex, birthday) values(?,?,?,?,?,?,?,?,?)",
                    new Object[]{user.getObjId(), user.getUserName(), user.getNikeName(),
                            user.getSign(), user.getIconUrl(), user.getTel(), user.getEmail(), user.getSex(), DateFmUtil.birthDateToLong(user.getBirthday())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateUser(User user) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {
            db.execSQL(
                    "update user set objId = ?, userName = ?, nikeName = ?, sign = ?, " +
                            "iconUrl = ?, tel = ?, email = ?, sex = ?, birthday = ? where " +
                            "objId = '" + user.getObjId() + "'",
                    new Object[]{user.getObjId(), user.getUserName(), user.getNikeName(),
                            user.getSign(), user.getIconUrl(), user.getTel(), user.getEmail(), user.getSex(), DateFmUtil.birthDateToLong(user.getBirthday())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void addFriend(User user) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {
            db.execSQL(
                    "insert into friend(objId, userName) values(?,?)",
                    new Object[]{user.getObjId(), user.getUserName()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteFriend(User user) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Log.d("del", "DELETE FROM friend where userName='" + user.getUserName() + "'");
        try {
            db.execSQL("DELETE FROM friend where userName='" + user.getUserName() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public boolean isFriend(String username) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("friend", null, "userName=?",
                new String[]{username}, null, null, null);
        boolean tmp = cursor.moveToFirst();
        db.close();
        return tmp;
    }


    public User findUserByName(String username) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, "username=?",
                new String[]{username}, null, null, null);

        if (!cursor.moveToFirst()) {
            db.close();
            return null;
        }

        User u = new User(cursor.getString(cursor.getColumnIndex("objId")), cursor.getString(cursor.getColumnIndex("userName"))
                , cursor.getString(cursor.getColumnIndex("nikeName")), cursor.getString(cursor.getColumnIndex("sign"))
                , cursor.getString(cursor.getColumnIndex("iconUrl")), cursor.getString(cursor.getColumnIndex("tel"))
                , cursor.getString(cursor.getColumnIndex("email")), cursor.getString(cursor.getColumnIndex("sex"))
                , cursor.getLong(cursor.getColumnIndex("birthday")) == 0 ? null : new Date(cursor.getLong(cursor.getColumnIndex("birthday"))));
        db.close();
        return u;

    }

    public void deleteLastMsgById(String conversationId) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM last_msg where conversation_id='" + conversationId + "'");
    }

    public void updateLastMsg(LastMessage message) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("lastTime", String.valueOf(message.getLastTime()));
            if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversation_id())) {
                values.put("nikeName", "群聊:" + message.getNikeName());
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

    public boolean isExistUser(String username) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, "username=?",
                new String[]{username}, null, null, null);
        boolean tmp = cursor.moveToFirst();
        db.close();
        return tmp;
    }

    public ArrayList<User> findAllFriend() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("friend,user", null, "friend.userName=user.userName",
                null, null, null, null);

        if (!cursor.moveToFirst()) {
            db.close();
            return null;
        }

        ArrayList<User> arrayList = new ArrayList<User>();

        for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {

            arrayList.add(new User(cursor.getString(cursor.getColumnIndex("objId")), cursor.getString(cursor.getColumnIndex("userName"))
                    , cursor.getString(cursor.getColumnIndex("nikeName")), cursor.getString(cursor.getColumnIndex("sign"))
                    , cursor.getString(cursor.getColumnIndex("iconUrl")), cursor.getString(cursor.getColumnIndex("tel"))
                    , cursor.getString(cursor.getColumnIndex("email")), cursor.getString(cursor.getColumnIndex("sex"))
                    , cursor.getLong(cursor.getColumnIndex("birthday")) == 0 ? null : new Date(cursor.getLong(cursor.getColumnIndex("birthday")))));

        }
        db.close();
        return arrayList;
    }


    public ArrayList<LastMessage> findAllLastMsg() {
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
                    , cursor.getLong(cursor.getColumnIndex("lastTime")), cursor.getString(cursor.getColumnIndex("lastMsg")),
                    cursor.getInt(cursor.getColumnIndex("unreadCount"))));
        }
        db.close();
        return arrayList;
    }

    public void updateUnreadCountById(String conversationId, boolean clear) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        if (clear) {
            try {
                db.execSQL("UPDATE last_msg set unreadCount = 0 where conversation_id =" +
                        "'" + conversationId + "'");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        } else {
            try {
                db.execSQL("UPDATE last_msg set unreadCount = unreadCount + 1 where conversation_id =" +
                        "'" + conversationId + "'");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }

}
