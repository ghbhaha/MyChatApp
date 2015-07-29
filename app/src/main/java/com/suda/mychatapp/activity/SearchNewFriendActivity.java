package com.suda.mychatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.FriendsBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.DisplayImageOptionsUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchNewFriendActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new_friend);

        initWidget();
    }

    public void searchNewFriend(View view) {
        String username = mEtfriendUsername.getText().toString();
        if (TextUtil.isTextEmpty(username)) {
            Toast.makeText(this, "你还没输入小伙伴的用户名呢", Toast.LENGTH_SHORT).show();
        } else {
            AVQuery<MyAVUser> query = AVObject.getQuery(MyAVUser.class);
            query.whereEqualTo("username", username);
            query.findInBackground(new FindCallback<MyAVUser>() {
                @Override
                public void done(List<MyAVUser> list, AVException e) {
                    if (e == null) {
                        mFriendUser = new User(list.get(0).getObjectId(), list.get(0).getUsername(), list.get(0).getNikename(),
                                list.get(0).getSign(), list.get(0).getIcon().getUrl(), list.get(0).getMobilePhoneNumber()
                                , list.get(0).getEmail(), list.get(0).getSex(), list.get(0).getBirthDay());

                        mRlFriend.setVisibility(View.VISIBLE);
                        mTvsign.setText(UserPropUtil.getSignByUser(mFriendUser));
                        mTvnikeName.setText(UserPropUtil.getNikeNameByUser(mFriendUser));
                        if (!TextUtil.isTextEmpty(mFriendUser.getIconUrl())) {
                            ImageLoader.getInstance().displayImage(mFriendUser.getIconUrl(), mHeadIcon, DisplayImageOptionsUtil.OPTION_1);
/*
                            ImageCacheUtil.showPicture(SearchNewFriendActivity.this, mFriendUser.getIconUrl(), new ImageCacheUtil.CallBack() {
                                @Override
                                public void done(Bitmap bitmap) {
                                    final Bitmap bm = bitmap;
                                    mHeadIcon.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mHeadIcon.setImageBitmap(bm);
                                        }
                                    });

                                }
                            });*/
                        }

                    } else {
                        mRlFriend.setVisibility(View.INVISIBLE);
                        mHeadIcon.setImageBitmap(null);
                        mTvnikeName.setText("");
                        mTvsign.setText("");
                        Toast.makeText(SearchNewFriendActivity.this, "没有发现这个小伙伴哟，赶快邀请注册吧", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void addUserToMine(View view) {
        Log.d("obj", mFriendUser.getObjId());

        if (MyApplication.getDBHelper().isFriend(mFriendUser.getUserName())) {
            Toast.makeText(this, UserPropUtil.getNikeNameByUser(mFriendUser) + "已经是你的好友了", Toast.LENGTH_SHORT).show();
            return;
        }
        if (MyAVUser.getCurrentUser().getUsername().equals(mFriendUser.getUserName())) {
            Toast.makeText(this, "不可添加自己哦", Toast.LENGTH_SHORT).show();
            return;
        }

        FriendsBus.starFriend(this, mFriendUser, new FriendsBus.ResultCallback() {
            @Override
            public void result(boolean rs) {
                if (rs) {
                    MyApplication.getDBHelper().addFriend(mFriendUser);
                    MyApplication.getmFriendsIFace().update();
                    Toast.makeText(SearchNewFriendActivity.this, "添加" + UserPropUtil.getNikeNameByUser(mFriendUser) + "成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getFriendInfo(View view) {
        Intent it = new Intent(SearchNewFriendActivity.this, FriendInfoActivity.class);
        it.putExtra(EXTRA_USERNAME, mFriendUser.getUserName());
        startActivity(it);
    }

    void initWidget() {
        mEtfriendUsername = (EditText) findViewById(R.id.et_friend_username);
        mRlFriend = (RelativeLayout) findViewById(R.id.new_friends_ll);
        mTvnikeName = (TextView) findViewById(R.id.tv_nikename);
        mTvsign = (TextView) findViewById(R.id.tv_sign);
        mHeadIcon = (CircleImageView) findViewById(R.id.icon);
    }

    private static User mFriendUser;
    private RelativeLayout mRlFriend;
    public TextView mTvnikeName;
    public TextView mTvsign;
    public CircleImageView mHeadIcon;
    private EditText mEtfriendUsername;
    private static final String EXTRA_USERNAME = "username";


}
