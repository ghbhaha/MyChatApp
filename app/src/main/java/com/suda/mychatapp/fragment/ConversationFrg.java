package com.suda.mychatapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.suda.mychatapp.utils.msg.MsgIFace;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.ChatActivity;
import com.suda.mychatapp.adapter.ConversationAdapter;
import com.suda.mychatapp.db.DbHelper;
import com.suda.mychatapp.db.pojo.LastMessage;
import com.suda.mychatapp.utils.msg.MessageHandler;

import java.util.ArrayList;


public class ConversationFrg extends Fragment {

    public ConversationFrg() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.conversation, container, false);
        initWidget(view);
        initEntity();
        return view;

    }

    void initWidget(View v) {
        mLastMsgLv = (SwipeMenuListView) v.findViewById(R.id.lv_conversation);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.id_swipe_ly);
    }


    public void reFresh() {
        mMessageList.clear();
        if (mDBHelper.findAllLastMsg() != null) {
            mMessageList.addAll(mDBHelper.findAllLastMsg());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    void initEntity() {
        mDBHelper = new DbHelper(getActivity());
        mMessageList = new ArrayList<LastMessage>();
        if (mDBHelper.findAllLastMsg() != null) {
            mMessageList.addAll(mDBHelper.findAllLastMsg());
        }
        mAdapter = new ConversationAdapter(getActivity(), mMessageList);
        mLastMsgLv.setAdapter(mAdapter);
        mLastMsgLv.setMenuCreator(creator);
        msgIFace = new MsgIFace() {
            @Override
            public void update() {
                reFresh();
            }

            @Override
            public void update(AVIMTextMessage message) {

            }
        };

        mLastMsgLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(getActivity(), ChatActivity.class);
                it.putExtra(EXTRA_USERNAME, mMessageList.get(position).getUserName());
                it.putExtra(EXTRA_CONVERSATION_ID, mMessageList.get(position).getConversation_id());
                startActivity(it);
            }
        });

        //拦截ViewPager滑动事件
        mLastMsgLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                        mViewPager.requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        if (mMessageList.size() != 0)
                            mViewPager.requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }
        });

        mLastMsgLv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0:
                        Intent it = new Intent(getActivity(), ChatActivity.class);
                        it.putExtra(EXTRA_USERNAME, mMessageList.get(position).getUserName());
                        it.putExtra(EXTRA_CONVERSATION_ID, mMessageList.get(position).getConversation_id());
                        startActivity(it);
                        break;
                    case 1:
                        mDBHelper.deleteLastMsgById(mMessageList.get(position).getConversation_id());
                        mMessageList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        MessageHandler.setiFace(msgIFace);

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getActivity());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                    0xCE)));
            openItem.setWidth(dp2px(90));
            openItem.setTitle("打开");
            openItem.setTitleSize(18);
            openItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(openItem);

            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getActivity());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            deleteItem.setWidth(dp2px(90));
            deleteItem.setTitle("删除");
            deleteItem.setTitleSize(18);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };


    public ConversationFrg setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        return this;
    }

    private ArrayList<LastMessage> mMessageList;
    private SwipeMenuListView mLastMsgLv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ConversationAdapter mAdapter;
    private DbHelper mDBHelper;
    private MsgIFace msgIFace;

    private ViewPager mViewPager;

    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";

}
