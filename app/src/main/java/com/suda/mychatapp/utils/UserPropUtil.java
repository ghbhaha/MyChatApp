package com.suda.mychatapp.utils;

import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;

/**
 * Created by Suda on 2015/7/23.
 */
public class UserPropUtil {

    public static String getNikeNameTipByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getNikename()) ? "还未填写哦" : user.getNikename();
    }

    public static String getTelTipByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getMobilePhoneNumber()) ? "还未填写哦" : user.getMobilePhoneNumber();
    }

    public static String getSexTipByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSex()) ? "还未填写哦" : user.getSex();
    }

    public static String getSignTipByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSign()) ? "还未填写哦" : user.getSign();
    }

    public static String getEmailTipByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getEmail()) ? "还未填写哦" : user.getEmail();
    }

    public static String getBirthDayTipByAVUser(MyAVUser user) {
        return user.getBirthDay() == null ? "还未填写哦" : DateFmUtil.fmDateBirth(user.getBirthDay());
    }

    public static String getNikeNameByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getNikename()) ? user.getUsername() : user.getNikename();
    }

    public static String getTelByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getMobilePhoneNumber()) ? "就不告诉你" : user.getMobilePhoneNumber();
    }

    public static String getBirthDayByAVUser(MyAVUser user) {
        return user.getBirthDay() == null ? "就不告诉你" : DateFmUtil.fmDateBirth(user.getBirthDay());
    }

    public static String getSexByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSex()) ? "你猜" : user.getSex();
    }

    public static String getEmailByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getEmail()) ? "就不告诉你" : user.getEmail();
    }

    public static String getSignByAVUser(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSign()) ? getNikeNameByAVUser(user) + "很懒，还未填写签名哦！" : user.getSign();
    }


    public static String getNikeNameByUser(User user) {
        return TextUtil.isTextEmpty(user.getNikeName()) ? user.getUserName() : user.getNikeName();
    }


    public static String getTelByUser(User user) {
        return TextUtil.isTextEmpty(user.getTel()) ? "就不告诉你" : user.getTel();
    }

    public static String getBirthDayByUser(User user) {
        return user.getBirthday() == null ? "就不告诉你" : DateFmUtil.fmDateBirth(user.getBirthday());
    }

    public static String getSexByUser(User user) {
        return TextUtil.isTextEmpty(user.getSex()) ? "你猜" : user.getSex();
    }

    public static String getEmailByUser(User user) {
        return TextUtil.isTextEmpty(user.getEmail()) ? "就不告诉你" : user.getEmail();
    }

    public static String getSignByUser(User user) {
        return TextUtil.isTextEmpty(user.getSign()) ? getNikeNameByUser(user) + "很懒，还未填写签名哦！" : user.getSign();
    }


}
