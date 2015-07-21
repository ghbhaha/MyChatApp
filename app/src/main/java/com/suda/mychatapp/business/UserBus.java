package com.suda.mychatapp.business;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.tencent.qc.stat.common.User;

import java.util.List;

/**
 * Created by Suda on 2015/7/19.
 */
public class UserBus {

    public static void getMe(final CallBack callBack) {
        if (me != null) {
            Log.d("cache","use");
            callBack.done(me);
        } else {
            AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
            query.whereEqualTo("objectId", MyAVUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<MyAVUser>() {
                @Override
                public void done(List<MyAVUser> list, AVException e) {
                    if (e == null) {
                        saveInCache(list.get(0));
                        callBack.done(list.get(0));
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    public static void clearMe(){
        me = null;
    }

    public static void refreshMe(final CallBack callBack) {

        AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
        query.whereEqualTo("objectId", MyAVUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<MyAVUser>() {
            @Override
            public void done(List<MyAVUser> list, AVException e) {
                if (e == null) {
                    saveInCache(list.get(0));
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

    public static void saveInCache(MyAVUser user) {
        me = user;
    }

    public static MyAVUser me;

    public interface CallBack {
        void done(MyAVUser me);
    }
}
