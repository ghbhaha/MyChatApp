package com.suda.mychatapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.suda.mychatapp.Conf;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.adapter.FaceGVAdapter;
import com.suda.mychatapp.adapter.FaceVPAdapter;
import com.suda.mychatapp.adapter.MessageAdapter;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.DbHelper;
import com.suda.mychatapp.db.pojo.LastMessage;
import com.suda.mychatapp.db.pojo.Message;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;
import com.suda.mychatapp.utils.msg.MessageHandler;
import com.suda.mychatapp.utils.msg.MessageUtil;
import com.suda.mychatapp.utils.msg.MsgIFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Suda on 2015/7/22.
 */
public class ChatActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.mDbHelper = new DbHelper(this);
        initWidget();
        initStaticFaces();
        InitViewPager();
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
                    if (mConversationId.equals(Conf.GROUP_CONVERSATION_ID)) {
                        filterGMessages(messages);
                    } else {
                        filterMessages(messages);
                        scrollToLast();
                    }
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
            }
        }
        mMessageAdapter.notifyDataSetChanged();
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


    private void filterGMessages(final List<AVIMMessage> messages) {
        for (final AVIMMessage message : messages) {
            if (message instanceof AVIMTypedMessage) {
                UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                    @Override
                    public void done(User user) {
                        mMessageList.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, user));
                        if (messages.size() == mMessageList.size()) {
                            mMessageList.clear();
                            for (final AVIMMessage message : messages) {
                                UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                                    @Override
                                    public void done(User user) {
                                        mMessageList.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, user));
                                        if (messages.size() == mMessageList.size()) {
                                            mMessageAdapter.notifyDataSetChanged();
                                            scrollToLast();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    private void filterGMessages2(final List<AVIMMessage> messages) {
        final List<Message> typedMessages = new ArrayList<Message>();
        if (messages.size() == 0) {
            toast("无更早的消息了");
            isLoadingMessages.set(false);
            mSwipeLayout.setRefreshing(false);
            return;
        }

        for (final AVIMMessage message : messages) {
            UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                        @Override
                        public void done(User user) {
                            typedMessages.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, user));
                            if (messages.size() == typedMessages.size()) {
                                typedMessages.clear();
                                for (final AVIMMessage message : messages) {
                                    UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                                        @Override
                                        public void done(User user) {
                                            typedMessages.add(MessageUtil.aviMsgtoMsg((AVIMTypedMessage) message, user));
                                            if (messages.size() == typedMessages.size()) {
                                                List<Message> newMessages = new ArrayList<Message>();
                                                newMessages.addAll(typedMessages);
                                                newMessages.addAll(mMessageList);
                                                mMessageList.clear();
                                                mMessageList.addAll(newMessages);
                                                mLvChat.setSelection(typedMessages.size() - 1);
                                                isLoadingMessages.set(false);
                                                mSwipeLayout.setRefreshing(false);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void sendText(View view) {
        if (isSendSuccess) {
            isSendSuccess = false;

            final AVIMTextMessage message = new AVIMTextMessage();
            if (!TextUtil.isTextEmpty(mEtMsg.getText().toString())) {
                message.setText(mEtMsg.getText().toString());
                mConversation.sendMessage(message, new AVIMConversationCallback() {
                    @Override
                    public void done(AVException e) {
                        if (null != e) {

                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    t.cancel();
                                    if (!isSendSuccess) {
                                        isSendSuccess = true;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ChatActivity.this, "发送超时,请重新发送", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }, 2 * 1000);

                            e.printStackTrace();
                        } else {

                            if (!mDbHelper.isExistMsg(message.getConversationId())) {
                                if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversationId())) {
                                    LastMessage lastMessage = new LastMessage(message.getConversationId(), mMe.getUserName(), UserPropUtil.getNikeNameByUser(mMe), mMe.getIconUrl(),
                                            message.getTimestamp(), message.getText());
                                    mDbHelper.addLastMess(lastMessage);
                                } else {
                                    LastMessage lastMessage = new LastMessage(message.getConversationId(), mFriend.getUserName(), UserPropUtil.getNikeNameByUser(mFriend), mFriend.getIconUrl(),
                                            message.getTimestamp(), message.getText());
                                    mDbHelper.addLastMess(lastMessage);
                                }
                            } else {
                                LastMessage lastMessage = new LastMessage(message.getConversationId(), mMe.getUserName(), UserPropUtil.getNikeNameByUser(mMe), mMe.getIconUrl(),
                                        message.getTimestamp(), message.getText());
                                mDbHelper.updateLastMsg(lastMessage);
                            }

                            mMessageList.add(MessageUtil.aviMsgtoMsg(message, mMe));
                            mMessageAdapter.notifyDataSetChanged();
                            finishSend();
                        }
                    }
                });
            } else {
                isSendSuccess = true;
            }
        }
    }

    public void finishSend() {
        mEtMsg.setText(null);
        isSendSuccess = true;
        if (MessageHandler.getiFace() != null) {
            MessageHandler.getiFace().update();
        }
        scrollToLast();
    }

    private void scrollToLast() {
        mLvChat.setSelection(mLvChat.getCount() - 1);
        //mLvChat.smoothScrollToPosition(mLvChat.getCount() - 1);
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
            mConversation.queryMessages(null, time, PAGE_SIZE, new AVIMMessagesQueryCallback() {
                @Override
                public void done(List<AVIMMessage> list, AVException e) {
                    if (filterException(e)) {
                        List<Message> typedMessages = new ArrayList<Message>();
                        if (mConversationId.equals(Conf.GROUP_CONVERSATION_ID)) {
                            filterGMessages2(list);
                            return;
                        } else {
                            typedMessages = filterMessages2(list);
                        }

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
        public void onMessage(final AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
            if (!(message instanceof AVIMTextMessage)) {
                return;
            }
            if (conversation.getConversationId().equals(ChatActivity.this.mConversation.getConversationId())) {
                if (mConversationId.equals(Conf.GROUP_CONVERSATION_ID)) {
                    UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                        @Override
                        public void done(User user) {
                            mMessageList.add(MessageUtil.aviMsgtoMsg(message, user));
                            mMessageAdapter.notifyDataSetChanged();
                            scrollToLast();
                        }
                    });
                } else {
                    mMessageList.add(MessageUtil.aviMsgtoMsg(message, mFriend));
                    mMessageAdapter.notifyDataSetChanged();
                }
                scrollToLast();
            }
        }
    }


    public void initEntity() {

        mOtherMsgTimer = new Timer();
        mMsgIFace = new MsgIFace() {
            @Override
            public void update() {

            }

            @Override
            public void update(final AVIMTextMessage message) {
                UserBus.findUser(message.getFrom(), new UserBus.CallBack2() {
                    @Override
                    public void done(User user) {

                        mOtherMsgTimer.cancel();
                        mOtherMsgTimer = new Timer();
                        mOtherMsgTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mFmLayoutOtherMsg.setVisibility(View.GONE);
                                        mOtherMsgTimer.cancel();
                                    }
                                });
                            }
                        }, 2000);

                        mOtherMsg = message;
                        mFmLayoutOtherMsg.setVisibility(View.VISIBLE);
                        if (Conf.GROUP_CONVERSATION_ID.equals(message.getConversationId())) {
                            mTvOtherMsg.setText("Suda聊天室" + UserPropUtil.getNikeNameByUser(user) + ":" + message.getText());
                        } else {
                            mTvOtherMsg.setText(UserPropUtil.getNikeNameByUser(user) + ":" + message.getText());
                        }
                    }
                });
            }
        };

        mConversation = MyApplication.getIMClient().getConversation(mConversationId);
        mMessageList = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, mMessageList);

        mLvChat.setAdapter(mMessageAdapter);

        mChatHandler = new ChatHandler();
        MessageHandler.setActivityMessageHandler(mChatHandler);
        if (!mConversationId.equals(Conf.GROUP_CONVERSATION_ID)) {
            MessageHandler.setCurrentFriend(mFriend.getUserName());
        } else {
            MessageHandler.setCurrentFriend("group");
        }
        UserBus.getMe(new UserBus.CallBack() {
            @Override
            public void done(MyAVUser user) {
                mMe = new User(user.getObjectId(), user.getUsername(), user.getNikename(), user.getSign(), user.getIcon().getUrl(), user.getMobilePhoneNumber()
                        , user.getEmail(), user.getSex(), user.getBirthDay());
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

        image_face = (ImageView) findViewById(R.id.image_face);

        image_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
            }
        });

        //表情布局
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);


        mFmLayoutOtherMsg = (FrameLayout) findViewById(R.id.other_msg);

        mFmLayoutOtherMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ChatActivity.this, ChatActivity.class);
                it.putExtra(EXTRA_CONVERSATION_ID, mOtherMsg.getConversationId());
                it.putExtra(EXTRA_USERNAME, mOtherMsg.getFrom());
                startActivity(it);
                ChatActivity.this.finish();
            }
        });

        mTvOtherMsg = (TextView) findViewById(R.id.tv_other_msg);

        mLvChat = (ListView) findViewById(R.id.chat_lv);

        mLvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mEtMsg = (EditText) findViewById(R.id.id_input_msg);

        mEtMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
            }
        });

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
        if (mConversationId.equals(Conf.GROUP_CONVERSATION_ID)) {
            getSupportActionBar().setTitle("Suda聊天室");
            initEntity();
        } else {
            UserBus.findUser(mFriendUserName, new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    mFriend = user;
                    getSupportActionBar().setTitle(String.format(getString(R.string.chatting_with), UserPropUtil.getNikeNameByUser(user)));
                    initEntity();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        MessageHandler.setIsBackTask(true);
        MessageHandler.setiOtherFace(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MessageHandler.setIsBackTask(false);
        MessageHandler.setiOtherFace(mMsgIFace);
        mDbHelper.updateUnreadCountById(mConversationId, true);
        if (MessageHandler.getiFace() != null) {
            MessageHandler.getiFace().update();
        }
        super.onResume();
    }


    /*
     * 初始表情 *
	 */
    private void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((mEtMsg.getText()));
        int iCursorEnd = Selection.getSelectionEnd((mEtMsg.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) mEtMsg.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((mEtMsg.getText()));
        ((Editable) mEtMsg.getText()).insert(iCursor, text);
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (mEtMsg.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(mEtMsg.getText());
            int iCursorStart = Selection.getSelectionStart(mEtMsg.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) mEtMsg.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) mEtMsg.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) mEtMsg.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     * *
     */
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = mEtMsg.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    /**
     * 初始化表情列表staticFacesList
     */
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }

    }


    public void hideSoftInputView() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
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
    private User mFriend;
    private User mMe;
    private ChatHandler mChatHandler;

    private boolean isSendSuccess = true;

    private SwipeRefreshLayout mSwipeLayout;

    private DbHelper mDbHelper;

    MsgIFace mMsgIFace;

    FrameLayout mFmLayoutOtherMsg;
    TextView mTvOtherMsg;
    AVIMTextMessage mOtherMsg;
    Timer mOtherMsgTimer;

    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private LinearLayout chat_face_container;
    private ImageView image_face;//表情图标

    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<View>();
    private List<String> staticFacesList;

}