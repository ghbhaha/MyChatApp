package com.suda.mychatapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.ChatActivity;
import com.suda.mychatapp.activity.MainActivity;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;

/**
 * Created by Suda on 2015/7/25.
 */
public class NotificationUtil {
    public static void createNotification(final Context ct) {

        UserBus.getMe(new UserBus.CallBack() {
            @Override
            public void done(final MyAVUser user) {
                if (user == null) {
                } else {
                    ImageCacheUtil.showPicture(ct, user.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
                        @Override
                        public void done(Bitmap bitmap) {
                            showNormalNotification(ct, UserPropUtil.getNikeNameByAVUser(user), bitmap);
                        }
                    });
                }
            }
        });
    }

    static void showNormalNotification(Context ct, String title, Bitmap icon) {

        NotificationManager notificationManager = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(ct)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.select_expression_normal)
                .setContentTitle(title)
                .setContentText(ct.getString(R.string.app_name) + "正在后台运行")
                .build();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(ct, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notification.flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻 Flag
        PendingIntent contextIntent = PendingIntent.getActivity(ct, 0, intent, 0);
        notification.contentIntent = contextIntent;
        notificationManager.notify(R.mipmap.chat_launcher, notification);
    }

    public static void clearNotification(Context ct) {
        NotificationManager notificationManager = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.mipmap.chat_launcher);
    }

    //当当前好友后台，来新消息时调用
    public static void showCurrentOneChatNotification(final Context context, final AVIMTextMessage message) {
        UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
            @Override
            public void done(final User user) {
                ImageCacheUtil.showPicture(context, user.getIconUrl(), new ImageCacheUtil.CallBack() {
                    @Override
                    public void done(Bitmap bitmap) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new Notification.Builder(context)
                                .setLargeIcon(bitmap)
                                .setSmallIcon(R.drawable.select_expression_normal)
                                .setContentTitle(UserPropUtil.getNikeNameByUser(user))
                                .setContentText(message.getText())
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


    //当新的好友来信息时调用
    public static void showNewOneChatNotification(final Context context, final AVIMTextMessage message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        final boolean sound = sharedPreferences.getBoolean("sounds",false);
        UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
            @Override
            public void done(final User user) {
                ImageCacheUtil.showPicture(context, user.getIconUrl(), new ImageCacheUtil.CallBack() {
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
                                .setContentTitle(UserPropUtil.getNikeNameByUser(user))
                                .setContentText(message.getText())
                                .build();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
                        if(sound){
                            notification.defaults = Notification.DEFAULT_SOUND;//声音默认
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(EXTRA_CONVERSATION_ID, message.getConversationId());
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

    //当当前好友后台，来新消息时调用
    public static void showCurrentGroupChatNotification(final Context context, final AVIMTextMessage message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean group_msg = sharedPreferences.getBoolean("group_msg",false);
        if(!group_msg){
            return;
        }
        UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
            @Override
            public void done(final User user) {
                ImageCacheUtil.showPicture(context, user.getIconUrl(), new ImageCacheUtil.CallBack() {
                    @Override
                    public void done(Bitmap bitmap) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new Notification.Builder(context)
                                .setLargeIcon(bitmap)
                                .setSmallIcon(R.drawable.select_expression_normal)
                                .setContentTitle("Suda聊天室" + UserPropUtil.getNikeNameByUser(user))
                                .setContentText(message.getText())
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


    //当新的好友来信息时调用
    public static void showNewGroupChatNotification(final Context context, final AVIMTextMessage message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        final boolean sound = sharedPreferences.getBoolean("sounds",false);
        boolean group_msg = sharedPreferences.getBoolean("group_msg",false);
        if(!group_msg){
            return;
        }
        UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
            @Override
            public void done(final User user) {
                ImageCacheUtil.showPicture(context, user.getIconUrl(), new ImageCacheUtil.CallBack() {
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
                                .setContentTitle("Suda聊天室" + UserPropUtil.getNikeNameByUser(user))
                                .setContentText(message.getText())
                                .build();
                        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
                        if(sound){
                            notification.defaults = Notification.DEFAULT_SOUND;//声音默认
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(EXTRA_CONVERSATION_ID, message.getConversationId());
                        intent.putExtra(EXTRA_USERNAME, "Suda聊天室");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.contentIntent = pendingIntent;
                        manager.notify(R.mipmap.chat_launcher, notification);
                    }
                });
            }
        });
    }


    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";
}
