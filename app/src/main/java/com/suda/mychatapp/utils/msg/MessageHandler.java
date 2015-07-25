package com.suda.mychatapp.utils.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.ChatActivity;
import com.suda.mychatapp.activity.MainActivity;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.ImageCacheUtil;
import com.suda.mychatapp.utils.UserPropUtil;

public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        Log.d(TAG, "消息已到达对方" + message.getContent());
    }

    @Override
    public void onMessage(final AVIMTypedMessage message, final AVIMConversation conversation, final AVIMClient client) {
        if (client.getClientId().equals(MyAVUser.getCurrentUser().getUsername())) {
            if (activityMessageHandler != null && message.getFrom().equals(currentFriend)) {
                // 正在聊天并且前台时，分发消息，刷新界面
                // 如果当前用户转到后台则通知消息，更新聊天记录
                if (isBackTask) {
                    if (message instanceof AVIMTextMessage) {
                        final AVIMTextMessage textMessage = (AVIMTextMessage) message;
                        UserBus.findUser(message.getFrom(), new UserBus.CallBack() {
                            @Override
                            public void done(final MyAVUser user) {
                                ImageCacheUtil.showPicture(context, user.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
                                    @Override
                                    public void done(Bitmap bitmap) {
                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        Notification notification = new Notification.Builder(context)
                                                .setLargeIcon(bitmap)
                                                .setSmallIcon(R.drawable.select_expression_normal)
                                                .setContentTitle(UserPropUtil.getNikeName(user))
                                                .setContentText(textMessage.getText())
                                                .build();
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                        intent.setClass(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                                        PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intent, 0);
                                        notification.contentIntent = contextIntent;
                                        notificationManager.notify(R.mipmap.chat_launcher, notification);
                                    }
                                });
                            }
                        });
                    }
                }
                activityMessageHandler.onMessage(message, conversation, client);
            } else {
                // 没有打开聊天界面或者不是当前联系人，通知栏通知
                if (message instanceof AVIMTextMessage) {
                    final AVIMTextMessage textMessage = (AVIMTextMessage) message;

                    UserBus.findUser(message.getFrom(), new UserBus.CallBack() {
                        @Override
                        public void done(final MyAVUser user) {
                            ImageCacheUtil.showPicture(context, user.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
                                @Override
                                public void done(Bitmap bitmap) {
                                    /**
                                     *  PendingIntent.FLAG_UPDATE_CURRENT
                                     *  Intent.FLAG_ACTIVITY_CLEAR_TOP
                                     *  保证每次传入的intent不一样
                                     */
                                    //获得通知管理器
                                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    Notification notification = new Notification.Builder(context)
                                            .setLargeIcon(bitmap)
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setContentTitle(UserPropUtil.getNikeName(user))
                                            .setContentText(textMessage.getText())
                                            .build();
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
                                    notification.defaults = Notification.DEFAULT_SOUND;//声音默认
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra(EXTRA_CONVERSATION_ID, conversation.getConversationId());
                                    intent.putExtra(EXTRA_USERNAME, message.getFrom());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    notification.contentIntent = pendingIntent;
                                    manager.notify(R.mipmap.chat_launcher, notification);
                                }
                            });
                        }
                    });
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

}
