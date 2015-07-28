package com.suda.mychatapp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.suda.mychatapp.utils.msg.MsgIFace;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.ChatActivity;
import com.suda.mychatapp.adapter.ConversationAdpter;
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
        mLastMsgLv = (ListView) v.findViewById(R.id.lv_conversation);
        SwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.id_swipe_ly);
    }


    public void reFresh(){
        mMessageList.clear();
        if (mDBhelper.findAllLastMsg() != null) {
            mMessageList.addAll(mDBhelper.findAllLastMsg());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    void initEntity() {
        mDBhelper = new DbHelper(getActivity());
        mMessageList = new ArrayList<LastMessage>();
        if(mDBhelper.findAllLastMsg()!=null){
            mMessageList.addAll(mDBhelper.findAllLastMsg());
        }
        mAdapter = new ConversationAdpter(getActivity(), mMessageList);
        mLastMsgLv.setAdapter(mAdapter);
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

        mLastMsgLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(getActivity()).setTitle("删除与"+mMessageList.get(position).getNikeName()+"的对话")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDBhelper.deleteLastMsgById(mMessageList.get(position).getConversation_id());
                                reFresh();
                            }
                        })
                        .create().show();
                return true;
            }
        });

        MessageHandler.setiFace(msgIFace);

    }

    private ArrayList<LastMessage> mMessageList;
    private ListView mLastMsgLv;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private ConversationAdpter mAdapter;
    private DbHelper mDBhelper;
    private MsgIFace msgIFace;

    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";

}
