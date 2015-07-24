package com.suda.mychatapp.utils.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.MainActivity;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
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
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        if (client.getClientId().equals(MyAVUser.getCurrentUser().getUsername())) {
            if (activityMessageHandler != null&&message.getFrom().equals(currentFriend)) {
                // 正在聊天时，分发消息，刷新界面
                activityMessageHandler.onMessage(message, conversation, client);
            } else {
                // 没有打开聊天界面或者不是当前联系人，这里简单地 Toast 一下。实际中可以刷新最近消息页面，增加小红点
                if (message instanceof AVIMTextMessage) {
                    final AVIMTextMessage textMessage = (AVIMTextMessage) message;
                    UserBus.findUser(message.getFrom(), new UserBus.CallBack() {
                        @Override
                        public void done(MyAVUser user) {

                            //获得通知管理器
                            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                            //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
                            Notification notification = new Notification(R.drawable.ic_launcher,"通知",System.currentTimeMillis());
                            //Intent intent = new Intent(context,MainActivity.class);
                            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            notification.setLatestEventInfo(context,  UserPropUtil.getNikeName(user) +"发来一条消息", textMessage.getText(), null);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
                            notification.defaults = Notification.DEFAULT_SOUND;//声音默认
                            manager.notify(12, notification);//发动通知,id由自己指定，每一个Notification对应的唯一标志
                            //其实这里的id没有必要设置,只是为了下面要用到它才进行了设置

                           // Toast.makeText(context, UserPropUtil.getNikeName(user) + " : " + textMessage.getText(), Toast.LENGTH_SHORT).show();
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

    private static AVIMTypedMessageHandler<AVIMTypedMessage> activityMessageHandler;
    private Context context;
    private String TAG = MessageHandler.this.getClass().getSimpleName();
    private static String currentFriend;

}
