package com.suda.mychatapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rey.material.widget.CheckBox;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.utils.DiskLruCache;

public class SettingsActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        msharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        mEditor = msharedPreferences.edit();

        mHavesound = msharedPreferences.getBoolean("sounds", false);
        mHavegroup_msg = msharedPreferences.getBoolean("group_msg", false);

        initWidget();


    }


    public void setSound(View view) {
        mEditor.putBoolean("sounds",!mHavesound);
        mHavesound = !mHavesound;
        mCbSound.setChecked(mHavesound);
        mEditor.commit();
    }

    public void setGroupMsg(View view) {
        mEditor.putBoolean("group_msg",!mHavegroup_msg);
        mHavegroup_msg = !mHavegroup_msg;
        mCbGroup.setChecked(mHavegroup_msg);
        mEditor.commit();
    }


    void initWidget() {
        mCbSound = (CheckBox) findViewById(R.id.cb_sound);
        mCbGroup = (CheckBox) findViewById(R.id.cb_group);

        mCbSound.setChecked(mHavesound);
        mCbGroup.setChecked(mHavegroup_msg);
    }

    private CheckBox mCbSound;
    private CheckBox mCbGroup;
    private SharedPreferences msharedPreferences;
    private SharedPreferences.Editor mEditor;

    boolean mHavesound;
    boolean mHavegroup_msg;

}
