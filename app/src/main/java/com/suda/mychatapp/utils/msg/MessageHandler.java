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
import com.suda.mychatapp.utils.NotificationUtil;
import com.suda.mychatapp.utils.UserPropUtil;

public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    DbHelper mDbhelper;

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

        if (message instanceof AVIMTextMessage) {
            final AVIMTextMessage textMessage = (AVIMTextMessage) message;

            UserBus.findUser(textMessage.getFrom(), new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    LastMessage lastMessage = new LastMessage(textMessage.getConversationId(), user.getUsername(), UserPropUtil.getNikeName(user), user.getIcon().getUrl(),
                             textMessage.getTimestamp(), textMessage.getText());
                    if (!mDbhelper.isExistMsg(message.getConversationId())) {
                        mDbhelper.addLastMess(lastMessage);
                    } else {
                        mDbhelper.updateLastMsg(lastMessage);
                    }
                    if(iFace!=null){
                        iFace.update();
                    }
                }
            });
        }

        if (client.getClientId().equals(MyAVUser.getCurrentUser().getUsername())) {
            if (activityMessageHandler != null && message.getFrom().equals(currentFriend)) {
                // 正在聊天并且前台时，分发消息，刷新界面
                // 如果当前用户转到后台则通知消息，更新聊天记录
                if (isBackTask) {
                    if (message instanceof AVIMTextMessage) {
                        AVIMTextMessage textMessage = (AVIMTextMessage) message;
                        NotificationUtil.showCurrentOneChatNotification(context, textMessage);
                    }
                }
                activityMessageHandler.onMessage(message, conversation, client);
            } else if (message.getConversationId().equals(Conf.GROUP_CONVERSATION_ID) && activityMessageHandler != null) {
                // 正在聊天并且前台时，分发消息，刷新界面
                // 如果当前用户转到后台则通知消息，更新聊天记录
                if (isBackTask) {
                    if (message instanceof AVIMTextMessage) {
                        AVIMTextMessage textMessage = (AVIMTextMessage) message;
                        NotificationUtil.showCurrentGroupChatNotification(context, textMessage);
                    }
                }
                activityMessageHandler.onMessage(message, conversation, client);
            } else {
                if (message.getConversationId().equals(Conf.GROUP_CONVERSATION_ID)) {
                    // 没有打开聊天界面或者不是当前联系人并且是群组，通知栏群组通知
                    if (message instanceof AVIMTextMessage) {
                        AVIMTextMessage textMessage = (AVIMTextMessage) message;
                        NotificationUtil.showNewGroupChatNotification(context, textMessage);
                    }
                }
                // 没有打开聊天界面或者不是当前联系人，通知栏通个人通知
                if (message instanceof AVIMTextMessage) {
                    final AVIMTextMessage textMessage = (AVIMTextMessage) message;
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

    public static MsgIFace getiFace() {
        return iFace;
    }

    public static void setiFace(MsgIFace iFace) {
        MessageHandler.iFace = iFace;
    }

    public static void setIsBackTask(Boolean isBackTask) {
        MessageHandler.isBackTask = isBackTask;
    }

    private static AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler;
    private Context context;
    private String TAG = MessageHandler.this.getClass().getSimpleName();
    private static String currentFriend;
    private static Boolean isBackTask = false;
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";
    private static MsgIFace iFace;


}
