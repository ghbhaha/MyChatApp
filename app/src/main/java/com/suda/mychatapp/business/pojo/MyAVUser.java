package com.suda.mychatapp.business.pojo;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;

import java.util.Date;

/**
 * Created by Suda on 2015/7/18.
 */

public class MyAVUser extends AVUser {

    public String getEmail() {
        return getString(EMAIL);
    }

    public void setEmail(String value) {
        put(EMAIL, value);
    }

    public String getUsername() {
        return getString(USERNAME);
    }

    public void setUsername(String value) {
        put(USERNAME, value);
    }

    public String getNikename() {
        return getString(NIKENAME);
    }

    public void setNikename(String value) {
        put(NIKENAME, value);
    }

    public String getPassword() {
        return getString(NIKENAME);
    }

    public void setPassword(String value) {
        put(PASSWORD, value);
    }

    public int getOld() {
        return getInt(OLD);
    }

    public void setOld(String value) {
        put(OLD, value);
    }

    public String getSex() {
        return getString(SEX);
    }

    public void setSex(String value) {
        put(SEX, value);
    }

    public AVFile getIcon() {
        return getAVFile(ICON);
    }

    public void setIcon(AVFile value) {
        put(ICON, value);
    }

    public int getSudaId() {
        return getInt(SUDAID);
    }

    public void setSudaId(String value) {
        put(SUDAID, value);
    }

    public String getSign() {
        return getString(SIGN);
    }

    public void setSign(String value) {
        put(SIGN, value);
    }

    public Date getBirthDay() {
        return getDate(BIRTHDAY);
    }

    public void setBirthDay(Date value) {
        put(BIRTHDAY, value);
    }

    private final static String EMAIL = "email";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String OLD = "old";
    private final static String SEX = "sex";
    private final static String ICON = "head_icon";
    private final static String SUDAID = "sudaid";
    private final static String SIGN="sign";
    private final static String NIKENAME="nikename";
    private final static String BIRTHDAY="birthday";

}
