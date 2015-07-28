package com.suda.mychatapp.db.pojo;

import java.util.Date;

/**
 * Created by Suda on 2015/7/26.
 */
public class LastMessage {

    private String conversation_id;
    private String userName;
    private String nikeName;
    private String iconUrl;
    private long lastTime;
    private String lastMsg;
    private int unreadCount;


    public LastMessage(String conversation_id, String userName, String nikeName, String iconUrl, long lastTime, String lastMsg, int unreadCount) {
        this.conversation_id = conversation_id;
        this.userName = userName;
        this.nikeName = nikeName;
        this.iconUrl = iconUrl;
        this.lastTime = lastTime;
        this.lastMsg = lastMsg;
        this.unreadCount = unreadCount;
    }

    public LastMessage(String conversation_id, String userName, String nikeName, String iconUrl, long lastTime, String lastMsg) {
        this.conversation_id = conversation_id;
        this.userName = userName;
        this.nikeName = nikeName;
        this.iconUrl = iconUrl;
        this.lastTime = lastTime;
        this.lastMsg = lastMsg;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public String getUserName() {
        return userName;
    }

    public long getLastTime() {
        return lastTime;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "LastMessage{" +
                "conversation_id='" + conversation_id + '\'' +
                ", userName='" + userName + '\'' +
                ", nikeName='" + nikeName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", lastTime=" + lastTime +
                ", lastMsg='" + lastMsg + '\'' +
                '}';
    }
}
