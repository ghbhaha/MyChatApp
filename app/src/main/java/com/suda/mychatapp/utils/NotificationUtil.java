package com.suda.mychatapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.MainActivity;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;

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
                            showNotification(ct, UserPropUtil.getNikeName(user), bitmap);
                        }
                    });
                }

            }
        });


    }

    static void showNotification(Context ct, String title, Bitmap icon) {
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

}
