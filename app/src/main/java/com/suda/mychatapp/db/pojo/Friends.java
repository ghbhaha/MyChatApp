package com.suda.mychatapp.db.pojo;

import android.graphics.Bitmap;

/**
 * Created by Suda on 2015/7/21.
 */
public class Friends {

    private String nikeName;
    private String sign;
    private String iconurl;
    private String username;


    public Friends(String nikeName, String sign,String username, String iconurl) {
        this.nikeName = nikeName;
        this.sign = sign;
        this.iconurl = iconurl;
        this.username = username;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    public String getNikeName() {
        return nikeName;
    }

    public String getSign() {
        return sign;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
