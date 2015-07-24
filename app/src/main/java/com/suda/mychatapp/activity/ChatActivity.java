package com.suda.mychatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.adapter.MessageAdapter;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;
import com.suda.mychatapp.utils.msg.MessageHandler;
import com.suda.mychatapp.utils.msg.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Suda on 2015/7/22.
 */
public class ChatActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initWidget();
    }

    private void loadMessagesWhenInit() {
        if (isLoadingMessages.get()) {
            return;
        }
        isLoadingMessages.set(true);
        mConversation.queryMessages(PAGE_SIZE, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> messages, AVException e) {
                if (filterException(e)) {
                    filterMessages(messages);
                    scrollToLast();
                }
                isLoadingMessages.set(false);
            }
        });
    }

    private void filterMessages(List<AVIMMessage> messages) {
        for (final AVIMMessage message : messages) {
            if (message instanceof AVIMTypedMessage) {
                if (message.getFrom().equals(mFriendUserName)) {
                    mMessageList.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, mFriend));
                } else {
                    mMessageList.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, mMe));
                }
                mMessageAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<Message> filterMessages2(List<AVIMMessage> messages) {
        List<Message> typedMessages = new ArrayList<Message>();
        for (AVIMMessage message : messages) {
            if (message.getFrom().equals(mFriendUserName)) {
                typedMessages.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, mFriend));
            } else {
                typedMessages.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, mMe));
            }
        }
        return typedMessages;
    }

    public void sendText(View view) {
        final AVIMTextMessage message = new AVIMTextMessage();
        if (!TextUtil.isTextEmpty(mEtMsg.getText().toString())) {
            message.setText(mEtMsg.getText().toString());
            mConversation.sendMessage(message, new AVIMConversationCallback() {
                @Override
                public void done(AVException e) {
                    if (null != e) {
                        e.printStackTrace();
                    } else {
                        mMessageList.add(MessageUtil.aviMsgtoMsg(message, mMe));
                        mMessageAdapter.notifyDataSetChanged();
                    }
                    finishSend();
                }

            });
        }
    }

    public void finishSend() {
        mEtMsg.setText(null);
        scrollToLast();
    }

    private void scrollToLast() {
        mLvChat.smoothScrollToPosition(mLvChat.getCount() - 1);
    }

    private void loadOldMessages() {
        if (isLoadingMessages.get() || mMessageList.size() < PAGE_SIZE) {
            toast("无更早的消息了");
            mSwipeLayout.setRefreshing(false);
            return;
        } else {
            isLoadingMessages.set(true);
            AVIMTypedMessage firstMsg = mMessageList.get(0).getAvimTypedMessage();
            long time = firstMsg.getTimestamp();
            Log.d("time", time + "");
            mConversation.queryMessages(null, time, PAGE_SIZE, new AVIMMessagesQueryCallback() {
                @Override
                public void done(List<AVIMMessage> list, AVException e) {
                    if (filterException(e)) {
                        List<Message> typedMessages = filterMessages2(list);
                        if (typedMessages.size() == 0) {
                            toast("无更早的消息了");
                        } else {
                            List<Message> newMessages = new ArrayList<Message>();
                            newMessages.addAll(typedMessages);
                            newMessages.addAll(mMessageList);
                            mMessageList.clear();
                            mMessageList.addAll(newMessages);
                            mLvChat.setSelection(typedMessages.size() - 1);
                        }
                    }
                    isLoadingMessages.set(false);
                    mSwipeLayout.setRefreshing(false);
                }
            });
        }
    }


    public class ChatHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

        public ChatHandler() {
        }

        @Override
        public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
            if (!(message instanceof AVIMTextMessage)) {
                return;
            }
            if (conversation.getConversationId().equals(ChatActivity.this.mConversation.getConversationId())) {
                mMessageList.add(MessageUtil.aviMsgtoMsg(message, mFriend));
                mMessageAdapter.notifyDataSetChanged();
                scrollToLast();
            }
        }
    }


    public void initEntity() {

        mConversation = MyApplication.getIMClient().getConversation(mConversationId);
        mMessageList = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, mMessageList);

        mLvChat.setAdapter(mMessageAdapter);

        mChatHandler = new ChatHandler();
        MessageHandler.setActivityMessageHandler(mChatHandler);
        MessageHandler.setCurrentFriend(mFriend.getUsername());

        UserBus.getMe(new UserBus.CallBack() {
            @Override
            public void done(MyAVUser user) {
                mMe = user;
                loadMessagesWhenInit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MessageHandler.setActivityMessageHandler(null);
        MessageHandler.setCurrentFriend(null);
        AVIMMessageManager.unregisterMessageHandler(AVIMTypedMessage.class, mChatHandler);
        super.onDestroy();
    }


    public void initWidget() {

        mLvChat = (ListView) findViewById(R.id.chat_lv);
        mEtMsg = (EditText) findViewById(R.id.id_input_msg);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLvChat.getChildCount() > 0) {
                    View first = mLvChat.getChildAt(0);
                    if (first != null && mLvChat.getFirstVisiblePosition() == 0 && first.getTop() == 0) {
                        loadOldMessages();
                    }
                }
            }
        });

        Intent it = getIntent();
        mFriendUserName = it.getStringExtra(EXTRA_USERNAME);
        mConversationId = it.getStringExtra(EXTRA_CONVERSATION_ID);
        UserBus.findUser(mFriendUserName, new UserBus.CallBack() {
            @Override
            public void done(MyAVUser user) {
                mFriend = user;
                getSupportActionBar().setTitle(String.format(getString(R.string.chatting_with), UserPropUtil.getNikeName(user)));
                initEntity();
            }
        });

    }

    private String mFriendUserName;
    private String mConversationId;
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";
    private static final String TAG = ChatActivity.class.getSimpleName();
    static final int PAGE_SIZE = 10;

    private ListView mLvChat;
    private MessageAdapter mMessageAdapter;
    private List<Message> mMessageList;
    private EditText mEtMsg;

    private AVIMConversation mConversation;
    private AtomicBoolean isLoadingMessages = new AtomicBoolean(false);
    private MyAVUser mFriend;
    private MyAVUser mMe;
    private ChatHandler mChatHandler;

    private SwipeRefreshLayout mSwipeLayout;
}