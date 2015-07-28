package com.suda.mychatapp.utils.msg;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.Conf;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.DbHelper;
import com.suda.mychatapp.db.pojo.LastMessage;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.NotificationUtil;
import com.suda.mychatapp.utils.UserPropUtil;

public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    public MessageHandler(Context context) {
        this.context = context;
        this.mDbhelper = new DbHelper(context);
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        Log.d(TAG, "消息已到达对方" + message.getContent());
    }

    @Override
    public void onMessage(final AVIMTypedMessage message, final AVIMConversation conversation, final AVIMClient client) {


        if (message instanceof AVIMTextMessage && client.getClientId().equals(MyAVUser.getCurrentUser().getUsername())) {
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;
            //更新数据库
            updateDb(message);

            //ChatActicity存在
            if (activityMessageHandler != null) {
                //如果是正在聊天的朋友
                if (message.getFrom().equals(currentFriend)) {
                    //如果后台
                    if (isBackTask) {
                        NotificationUtil.showCurrentOneChatNotification(context, textMessage);
                    }
                    activityMessageHandler.onMessage(message, conversation, client);

                    //如果是正在聊天的是群组
                } else if ("group".equals(currentFriend)) {
                    //如果后台
                    if (isBackTask) {
                        NotificationUtil.showCurrentGroupChatNotification(context, textMessage);
                    }
                    activityMessageHandler.onMessage(message, conversation, client);
                    //其他人发来消息
                } else {
                    iOtherFace.update(textMessage);
                }
                //ChatActicity不存在
            } else {
                //如果是群组信息
                if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversationId())) {
                    NotificationUtil.showNewGroupChatNotification(context, textMessage);
                    //如果是好友信息
                } else {
                    NotificationUtil.showNewOneChatNotification(context, textMessage);
                }
            }
        } else {
            client.close(null);
        }
    }

    public static AVIMTypedMessageHandler<AVIMTypedMessage> getActivityMessageHandler() {
        return activityMessageHandler;
    }

    public static void setActivityMessageHandler(AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler) {
        MessageHandler.activityMessageHandler = activityMessageHandler;
    }

    public static void setCurrentFriend(String currentFriend) {
        MessageHandler.currentFriend = currentFriend;
    }


    public static void setiOtherFace(MsgIFace iOtherFace) {
        MessageHandler.iOtherFace = iOtherFace;
    }

    public static MsgIFace getiFace() {
        return iFace;
    }

    public static void setiFace(MsgIFace iFace) {
        MessageHandler.iFace = iFace;
    }

    public static void setIsBackTask(Boolean isBackTask) {
        MessageHandler.isBackTask = isBackTask;
    }

    public void updateDb(final AVIMTypedMessage message) {
        if (message instanceof AVIMTextMessage) {
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;

            UserBus.findUser(textMessage.getFrom(), new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    LastMessage lastMessage = new LastMessage(textMessage.getConversationId(), user.getUserName(), UserPropUtil.getNikeNameByUser(user), user.getIconUrl(),
                            textMessage.getTimestamp(), textMessage.getText());
                    if (!mDbhelper.isExistMsg(message.getConversationId())) {
                        mDbhelper.addLastMess(lastMessage);
                        mDbhelper.updateUnreadCountById(message.getConversationId(), false);
                    } else {
                        mDbhelper.updateLastMsg(lastMessage);
                        mDbhelper.updateUnreadCountById(message.getConversationId(), false);
                    }
                    if (iFace != null) {
                        iFace.update();
                    }
                }
            });
        }
    }

    private static AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler;
    private Context context;
    private String TAG = MessageHandler.this.getClass().getSimpleName();
    private static String currentFriend;
    private static Boolean isBackTask = false;
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";
    private static MsgIFace iFace;
    private static MsgIFace iOtherFace;
    private DbHelper mDbhelper;

}
