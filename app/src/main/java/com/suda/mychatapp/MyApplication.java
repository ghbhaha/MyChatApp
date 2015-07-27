package com.suda.mychatapp;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.suda.mychatapp.business.LoadLib;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.DbHelper;
import com.suda.mychatapp.utils.msg.MessageHandler;

/**
 * Created by Suda on 2015/7/18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
        LoadLib.LoadLib();

        mDbHelper = new DbHelper(this);

        AVOSCloud.initialize(this, "bbi2udim376ydh5lvhq6jzp4o2afosu9nndydes45jvolhj4", "flbtai7ocvvvrsutun5k77jkgagvayew944mnms8e94u3z6j");
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(this));

    }

    public static AVIMClient getIMClient() {
        return AVIMClient.getInstance(MyAVUser.getCurrentUser().getUsername());
    }

    public static DbHelper getDBHelper() {
        return mDbHelper;
    }

    public static DbHelper mDbHelper;

    private final static String APP_ID = "bbi2udim376ydh5lvhq6jzp4o2afosu9nndydes45jvolhj4";
    private final static String APP_KEY = "flbtai7ocvvvrsutun5k77jkgagvayew944mnms8e94u3z6j";

}
