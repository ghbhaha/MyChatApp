package com.suda.mychatapp.fragment;

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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFriendship;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.callback.AVFriendshipCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.activity.ChatActivity;
import com.suda.mychatapp.adapter.FriendsAdapter;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.iface.FriendsIFace;
import com.suda.mychatapp.utils.FriendSortUtil;
import com.suda.mychatapp.utils.msg.ConversationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


public class FriendsFrg extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public FriendsFrg() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFriendsList = new ArrayList<User>();
        friendsAdapter = new FriendsAdapter(getActivity(), mFriendsList);

        handler = new android.os.Handler();

        mFriendsIFace = new FriendsIFace() {
            @Override
            public void update() {
                if (MyApplication.getDBHelper().findAllFriend() != null) {
                    mFriendsList.clear();
                    mFriendsList.addAll(MyApplication.getDBHelper().findAllFriend());
                    FriendSortUtil.sortFriend(mFriendsList);
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        };

        MyApplication.setFriendsIface(mFriendsIFace);

    }


    public void startChat(final String username) {
        fetchConversationWithClientIds(Arrays.asList(username), ConversationType.OneToOne, new
                AVIMConversationCreatedCallback
                        () {
                    @Override
                    public void done(AVIMConversation conversation, AVException e) {
                        if (e == null) {
                            Intent it = new Intent(getActivity(), ChatActivity.class);
                            it.putExtra(EXTRA_USERNAME, username);
                            it.putExtra(EXTRA_CONVERSATION_ID, conversation.getConversationId());
                            startActivity(it);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend, container, false);
        initWidget(view);
        initEntity();
        return view;
    }

    public void initWidget(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLvFriends = (ListView) view.findViewById(R.id.lv_friends);
        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startChat(mFriendsList.get(position).getUserName());
            }
        });
    }

    public void initEntity() {
        mLvFriends.setAdapter(friendsAdapter);
        if (MyApplication.getDBHelper().findAllFriend() != null) {
            mFriendsList.addAll(MyApplication.getDBHelper().findAllFriend());
            FriendSortUtil.sortFriend(mFriendsList);
            friendsAdapter.notifyDataSetChanged();
        } else {
            MyAVUser.getCurrentUser().friendshipQuery().getInBackground(new AVFriendshipCallback() {
                @Override
                public void done(final AVFriendship avFriendship, AVException e) {
                    if (e == null) {
                        for (int i = 0; i < avFriendship.getFollowees().size(); i++) {
                            AVUser user = (AVUser) avFriendship.getFollowees().get(i);
                            UserBus.findUser(user.getUsername(), new UserBus.CallBack2() {
                                @Override
                                public void done(User user) {
                                    if (!MyApplication.getDBHelper().isFriend(user.getUserName())) {
                                        MyApplication.getDBHelper().addFriend(user);
                                    }
                                    mFriendsList.add(user);
                                    if (mFriendsList.size() == avFriendship.getFollowees().size()) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                FriendSortUtil.sortFriend(mFriendsList);
                                                friendsAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else
                        e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        mFriendsList.clear();
        MyAVUser.getCurrentUser().friendshipQuery().getInBackground(new AVFriendshipCallback() {
            @Override
            public void done(final AVFriendship avFriendship, AVException e) {
                if (e == null) {
                    if (avFriendship.getFollowees().size() == 0) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        for (int i = 0; i < avFriendship.getFollowees().size(); i++) {
                            AVUser user = (AVUser) avFriendship.getFollowees().get(i);
                            UserBus.findUser(user.getUsername(), new UserBus.CallBack2() {
                                @Override
                                public void done(User user) {
                                    mFriendsList.add(user);
                                    if (!MyApplication.getDBHelper().isFriend(user.getUserName())) {
                                        MyApplication.getDBHelper().addFriend(user);
                                    }
                                    if (mFriendsList.size() == avFriendship.getFollowees().size()) {

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                FriendSortUtil.sortFriend(mFriendsList);
                                                mSwipeRefreshLayout.setRefreshing(false);
                                                friendsAdapter.notifyDataSetChanged();
                                                mLvFriends.setSelection(0);
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    }
                } else
                    e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchConversationWithClientIds(List<String> clientIds, final ConversationType type, final AVIMConversationCreatedCallback
            callback) {
        final AVIMClient imClient = MyApplication.getIMClient();
        final List<String> queryClientIds = new ArrayList<String>();
        queryClientIds.addAll(clientIds);
        if (!clientIds.contains(imClient.getClientId())) {
            queryClientIds.add(imClient.getClientId());
        }
        AVIMConversationQuery query = imClient.getQuery();
        query.whereEqualTo(Conversation.ATTRIBUTE_MORE + ".type", type.getValue());
        query.whereContainsAll(Conversation.COLUMN_MEMBERS, queryClientIds);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVException e) {
                if (e != null) {
                    callback.done(null, e);
                } else {
                    if (list == null || list.size() == 0) {
                        Map<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put(ConversationType.KEY_ATTRIBUTE_TYPE, type.getValue());
                        imClient.createConversation(queryClientIds, attributes, callback);
                    } else {
                        callback.done(list.get(0), null);
                    }
                }
            }
        });
    }


    private android.os.Handler handler;
    private ListView mLvFriends;
    private ArrayList<User> mFriendsList;
    private FriendsAdapter friendsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static FriendsIFace mFriendsIFace;


    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";


}
