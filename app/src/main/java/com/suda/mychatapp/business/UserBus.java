package com.suda.mychatapp.business;

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

    public void getMe(final CallBack callBack,MyAVUser user) {
        if (this.me != null) {
            callBack.done(this.me);
        } else {
            AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
            query.whereContains("username", user.getUsername());
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

    public void saveInCache(MyAVUser me) {
        this.me = me;
    }

    public static MyAVUser me;

    public interface CallBack{
         void done(MyAVUser me);
    }
}
