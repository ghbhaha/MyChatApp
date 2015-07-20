package com.suda.mychatapp;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.suda.mychatapp.business.LoadLib;

/**
 * Created by Suda on 2015/7/18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
        LoadLib.LoadLib();
        AVOSCloud.initialize(this,"bbi2udim376ydh5lvhq6jzp4o2afosu9nndydes45jvolhj4","flbtai7ocvvvrsutun5k77jkgagvayew944mnms8e94u3z6j");

    }


    private final static String APP_ID = "bbi2udim376ydh5lvhq6jzp4o2afosu9nndydes45jvolhj4";
    private final static String APP_KEY = "flbtai7ocvvvrsutun5k77jkgagvayew944mnms8e94u3z6j";

}
