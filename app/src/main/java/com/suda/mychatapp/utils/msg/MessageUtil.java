package com.suda.mychatapp.utils.msg;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.UserPropUtil;

/**
 * Created by Suda on 2015/7/23.
 */
public class MessageUtil {
    public static Message aviMsgtoMsg(AVIMTypedMessage avimTypedMessage, User user) {
        return new Message(user.getIconUrl(), UserPropUtil.getNikeNameByUser(user), avimTypedMessage);
    }
}
