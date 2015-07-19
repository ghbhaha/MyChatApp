package com.suda.mychatapp.util;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Suda on 2015/7/19.
 */
public class UiUtil {

    public static void  setNullElevation(ActionBarActivity aba){
        aba.getSupportActionBar().setElevation(0);
    }

    public static void setActionBarBack(ActionBarActivity aba){
        aba.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
