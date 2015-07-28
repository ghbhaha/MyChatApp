package com.suda.mychatapp.business;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Suda on 2015/7/19.
 */
public class UserBus {

    public static void getMe(final CallBack callBack) {
        if (me != null) {
            callBack.done(me);
        } else {
            if (MyAVUser.getCurrentUser() == null) {
                return;
            }
            AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
            query.whereEqualTo("objectId", MyAVUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<MyAVUser>() {
                @Override
                public void done(List<MyAVUser> list, AVException e) {
                    if (e == null) {
                        saveMeInCache(list.get(0));
                        callBack.done(list.get(0));
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void findUser(final String username, final CallBack2 callBack) {

        if (userHashMap == null)
            userHashMap = new HashMap<>();

        //查内存
        if (userHashMap.containsKey(username)) {
            callBack.done(userHashMap.get(username));
        } else {
            if (MyApplication.getDBHelper().isExistUser(username)) {
                //查本地，缓存到内存
                callBack.done(MyApplication.getDBHelper().findUserByName(username));
                userHashMap.put(username, MyApplication.getDBHelper().findUserByName(username));
            }

            //查网络，更新本地，更新内存
            AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
            query.whereEqualTo("username", username);
            query.findInBackground(new FindCallback<MyAVUser>() {
                @Override
                public void done(List<MyAVUser> list, AVException e) {
                    if (e == null) {
                        User u = new User(list.get(0).getObjectId(), list.get(0).getUsername(), list.get(0).getNikename(),
                                list.get(0).getSign(), list.get(0).getIcon().getUrl(), list.get(0).getMobilePhoneNumber(),
                                list.get(0).getEmail(), list.get(0).getSex(), list.get(0).getBirthDay());

                        if (!MyApplication.getDBHelper().isExistUser(list.get(0).getUsername())) {
                            MyApplication.getDBHelper().addUser(u);
                        } else {
                            MyApplication.getDBHelper().updateUser(u);
                        }
                        if (!userHashMap.containsKey(username)) {
                            callBack.done(u);
                        }
                        userHashMap.put(username, u);

                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void clearMe() {
        me = null;
    }

    public static void refreshMe(final CallBack callBack) {

        AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
        query.whereEqualTo("objectId", MyAVUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<MyAVUser>() {
            @Override
            public void done(List<MyAVUser> list, AVException e) {
                if (e == null) {
                    saveMeInCache(list.get(0));
                    callBack.done(list.get(0));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isExistLocalUser() {
        return MyAVUser.getCurrentUser() != null;
    }

    public static void saveMeInCache(MyAVUser user) {
        me = user;
    }

    public static MyAVUser me;

    public static HashMap<String, User> userHashMap;

    public interface CallBack {
        void done(MyAVUser user);
    }

    public interface CallBack2 {
        void done(User user);
    }
}
