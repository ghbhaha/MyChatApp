package com.suda.mychatapp.db.pojo;

import android.graphics.Bitmap;

/**
 * Created by Suda on 2015/7/21.
 */
public class Friends {

    private String nikeName;
    private String sign;
    private String userName;
    private Bitmap icon;

    public Friends(String nikeName, String sign, String userName, Bitmap icon) {
        this.nikeName = nikeName;
        this.sign = sign;
        this.userName = userName;
        this.icon = icon;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNikeName() {
        return nikeName;
    }

    public String getUserName() {
        return userName;
    }

    public String getSign() {
        return sign;
    }
}
