package com.suda.mychatapp.utils.msg;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.utils.UserPropUtil;

/**
 * Created by Suda on 2015/7/23.
 */
public class MessageUtil {
    public static Message aviMsgtoMsg(AVIMTypedMessage avimTypedMessage, MyAVUser user) {
        return new Message(user.getIcon().getUrl(), UserPropUtil.getNikeName(user), avimTypedMessage);
    }
}
