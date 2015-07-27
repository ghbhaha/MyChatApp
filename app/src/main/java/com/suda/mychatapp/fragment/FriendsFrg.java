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
import com.suda.mychatapp.iface.FriendsIface;
import com.suda.mychatapp.utils.FriendSortUtil;
import com.suda.mychatapp.utils.msg.ConversationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendsFrg extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public FriendsFrg() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendslist = new ArrayList<User>();
        mFriendsIface = new FriendsIface() {
            @Override
            public void update() {
                if (MyApplication.getDBHelper().findAllFriend() != null) {
                    mFriendslist.clear();
                    mFriendslist.addAll(MyApplication.getDBHelper().findAllFriend());
                    FriendSortUtil.sortFriend(mFriendslist);
                    friendsAdpter = new FriendsAdapter(getActivity(), mFriendslist);
                    mLvfriends.setAdapter(friendsAdpter);
                }
            }
        };

        MyApplication.setFriendsIface(mFriendsIface);

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
        mSwipeRefreshLayput = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
        mSwipeRefreshLayput.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayput.setOnRefreshListener(this);
        mLvfriends = (ListView) view.findViewById(R.id.lv_friends);
        mLvfriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startChat(mFriendslist.get(position).getUserName());
            }
        });
    }

    public void initEntity() {
        if (MyApplication.getDBHelper().findAllFriend() != null) {
            mFriendslist.addAll(MyApplication.getDBHelper().findAllFriend());
            FriendSortUtil.sortFriend(mFriendslist);
            friendsAdpter = new FriendsAdapter(getActivity(), mFriendslist);
            mLvfriends.setAdapter(friendsAdpter);
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
                                    mFriendslist.add(user);
                                    if (mFriendslist.size() == avFriendship.getFollowees().size()) {
                                        FriendSortUtil.sortFriend(mFriendslist);
                                        friendsAdpter = new FriendsAdapter(getActivity(), mFriendslist);
                                        mLvfriends.setAdapter(friendsAdpter);
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
        MyAVUser.getCurrentUser().friendshipQuery().getInBackground(new AVFriendshipCallback() {
            @Override
            public void done(final AVFriendship avFriendship, AVException e) {
                if (e == null) {
                    mFriendslist.clear();
                    if (avFriendship.getFollowees().size() == 0) {
                        mSwipeRefreshLayput.setRefreshing(false);
                        if (friendsAdpter != null) {
                            friendsAdpter.notifyDataSetChanged();
                        }
                    } else {
                        for (int i = 0; i < avFriendship.getFollowees().size(); i++) {
                            AVUser user = (AVUser) avFriendship.getFollowees().get(i);
                            UserBus.findUser(user.getUsername(), new UserBus.CallBack2() {
                                @Override
                                public void done(User user) {
                                    mFriendslist.add(user);
                                    if (!MyApplication.getDBHelper().isFriend(user.getUserName())) {
                                        MyApplication.getDBHelper().addFriend(user);
                                    }
                                    if (mFriendslist.size() == avFriendship.getFollowees().size()) {
                                        if (friendsAdpter != null) {
                                            FriendSortUtil.sortFriend(mFriendslist);
                                            friendsAdpter.notifyDataSetChanged();
                                        } else {
                                            FriendSortUtil.sortFriend(mFriendslist);
                                            friendsAdpter = new FriendsAdapter(getActivity(), mFriendslist);
                                            mLvfriends.setAdapter(friendsAdpter);
                                        }
                                        mSwipeRefreshLayput.setRefreshing(false);
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


    private ListView mLvfriends;
    private ArrayList<User> mFriendslist;
    private FriendsAdapter friendsAdpter;
    private SwipeRefreshLayout mSwipeRefreshLayput;

    public static FriendsIface mFriendsIface;


    private static final String EXTRA_CONVERSATION_ID = "conversation_id";
    private static final String EXTRA_USERNAME = "username";


}
