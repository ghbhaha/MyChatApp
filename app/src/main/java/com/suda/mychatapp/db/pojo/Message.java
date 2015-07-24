package com.suda.mychatapp.db.pojo;

import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.Date;

/**
 * Created by Suda on 2015/7/22.
 */
public class Message {
    private String iconurl;
    private String nikename;
    private AVIMTypedMessage avimTypedMessage;
    private Date date;


    public Message(String iconurl, String nikename, AVIMTypedMessage avimTypedMessage) {
        this.iconurl = iconurl;
        this.nikename = nikename;
        this.avimTypedMessage = avimTypedMessage;
    }

    public void setAvimTypedMessage(AVIMTypedMessage avimTypedMessage) {
        this.avimTypedMessage = avimTypedMessage;
    }

    public AVIMTypedMessage getAvimTypedMessage() {
        return avimTypedMessage;
    }

    public String getUsername() {
        return avimTypedMessage.getFrom();
    }

    public String getMsg() {
        if (AVIMReservedMessageType.getAVIMReservedMessageType(avimTypedMessage.getMessageType()) == AVIMReservedMessageType.TextMessageType) {
            AVIMTextMessage textMessage = (AVIMTextMessage) avimTypedMessage;
           return ((AVIMTextMessage) avimTypedMessage).getText();
        } else {
           return avimTypedMessage.getContent();
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
