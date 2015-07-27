package com.suda.mychatapp.utils.msg;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
/**
 * Created by Suda on 2015/7/26.
 */
public interface MsgIFace {
    void update();
    void update(AVIMTextMessage message);
}
