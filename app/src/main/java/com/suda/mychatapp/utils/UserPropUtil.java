package com.suda.mychatapp.utils;

import com.suda.mychatapp.business.pojo.MyAVUser;

/**
 * Created by Suda on 2015/7/23.
 */
public class UserPropUtil {

    public static String getNikeNameTip(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getNikename()) ? "还未填写哦" : user.getNikename();
    }

    public static String getTelTip(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getMobilePhoneNumber()) ? "还未填写哦" : user.getMobilePhoneNumber();
    }

    public static String getSexTip(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSex()) ? "还未填写哦" : user.getSex();
    }

    public static String getSignTip(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSign()) ? "还未填写哦" : user.getSign();
    }

    public static String getEmailTip(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getEmail()) ? "还未填写哦" : user.getEmail();
    }

    public static String getBirthDayTip(MyAVUser user) {
        return user.getBirthDay() == null ? "还未填写哦" : DateFmUtil.fmDate1(user.getBirthDay());
    }


    public static String getNikeName(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getNikename()) ? user.getUsername() : user.getNikename();
    }

    public static String getTel(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getMobilePhoneNumber()) ? "就不告诉你" : user.getMobilePhoneNumber();
    }

    public static String getBirthDay(MyAVUser user) {
        return user.getBirthDay() == null ? "就不告诉你" : DateFmUtil.fmDate1(user.getBirthDay());
    }

    public static String getSex(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSex()) ? "你猜" : user.getSex();
    }

    public static String getEmail(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getEmail()) ? "就不告诉你" : user.getEmail();
    }

    public static String getSign(MyAVUser user) {
        return TextUtil.isTextEmpty(user.getSign()) ? getNikeName(user) + "很懒，还未填写签名哦！" : user.getSign();
    }


}
