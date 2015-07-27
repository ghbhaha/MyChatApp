package com.suda.mychatapp.business;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FollowCallback;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;

/**
 * Created by Suda on 2015/7/24.
 */
public class FriendsBus {

    public static void starFriend(final Context ct, User friend, final ResultCallback callback) {
        MyAVUser.getCurrentUser().followInBackground(friend.getObjId(), new FollowCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    callback.result(true);
                    Toast.makeText(ct, "添加成功，返回刷新一下吧", Toast.LENGTH_SHORT).show();
                } else {
                    callback.result(false);
                    e.printStackTrace();
                }
            }

            @Override
            protected void internalDone0(Object o, AVException e) {
                if (e == null) {
                    callback.result(true);
                    Toast.makeText(ct, "添加成功，返回刷新一下吧", Toast.LENGTH_SHORT).show();
                } else {
                    callback.result(false);
                    e.printStackTrace();
                }
            }
        });
    }

    public static void unStarFriend(final Context ct, User friend, final ResultCallback callback) {
        MyAVUser.getCurrentUser().unfollowInBackground(friend.getObjId(), new FollowCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    Toast.makeText(ct, "取消成功，返回刷新一下吧", Toast.LENGTH_SHORT).show();
                    callback.result(true);
                } else {
                    callback.result(false);
                    e.printStackTrace();
                }
            }

            @Override
            protected void internalDone0(Object o, AVException e) {
                if (e == null) {
                    callback.result(true);
                    Toast.makeText(ct, "取消成功，返回刷新一下吧", Toast.LENGTH_SHORT).show();
                } else {
                    callback.result(false);
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ResultCallback {
        void result(boolean rs);
    }
}
