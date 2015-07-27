package com.suda.mychatapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.suda.mychatapp.utils.NotificationUtil;
import com.suda.mychatapp.utils.UIUtil;

/**
 * Created by Suda on 2015/7/19.
 */
public abstract class AbstructActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtil.setNullElevation(this);
        UIUtil.setActionBarBack(this);
    }

    @Override
    protected void onPause() {
        NotificationUtil.createNotification(this);
        AVAnalytics.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        NotificationUtil.clearNotification(this);
        AVAnalytics.onResume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        NotificationUtil.clearNotification(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    protected void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
