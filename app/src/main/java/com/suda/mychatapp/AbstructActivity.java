package com.suda.mychatapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.suda.mychatapp.util.UiUtil;

/**
 * Created by Suda on 2015/7/19.
 */
public abstract class AbstructActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtil.setNullElevation(this);
        UiUtil.setActionBarBack(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
