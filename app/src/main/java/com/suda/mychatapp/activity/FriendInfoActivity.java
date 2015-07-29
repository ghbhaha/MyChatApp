package com.suda.mychatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.FriendsBus;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.DisplayImageOptionsUtil;
import com.suda.mychatapp.utils.UserPropUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInfoActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        initWidget();
        initEntity();
    }


    void initWidget() {
        mStar = (FloatingActionButton) findViewById(R.id.star_friend);
        mHeadIcon = (CircleImageView) findViewById(R.id.profile_image);
        mTvSign = (TextView) findViewById(R.id.tv_sign);
        mTvUsername = (TextView) findViewById(R.id.tv_username);
        mTvSex = (TextView) findViewById(R.id.tv_sex);
        mTvTel = (TextView) findViewById(R.id.tv_tel);
        mTvNikeName = (TextView) findViewById(R.id.tv_nikename);
        mTvBirthDay = (TextView) findViewById(R.id.tv_birth);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
    }

    void initEntity() {
        Intent it = getIntent();
        username = it.getStringExtra(EXTRA_USERNAME);

        if (MyAVUser.getCurrentUser().getUsername().equals(username)) {
            mStar.setVisibility(View.INVISIBLE);
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    showInfoByAVUser(user);
                }
            });

        } else {
            UserBus.findUser(username, new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    mFriend = user;
                    showInfoByUser(user);
                    initStarIcon(user);
                }
            });
        }
    }


    public void setOrNotFriend(View View) {
        if (isFriend) {
            FriendsBus.unStarFriend(this, mFriend, new FriendsBus.ResultCallback() {
                @Override
                public void result(boolean rs) {
                    MyApplication.getDBHelper().deleteFriend(mFriend);
                    MyApplication.getmFriendsIFace().update();
                    isFriend = !rs;
                    mStar.setIcon(isFriend ?
                            R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                }
            });
        } else {
            FriendsBus.starFriend(this, mFriend, new FriendsBus.ResultCallback() {
                @Override
                public void result(boolean rs) {
                    MyApplication.getDBHelper().addFriend(mFriend);
                    MyApplication.getmFriendsIFace().update();
                    isFriend = rs;
                    mStar.setIcon(isFriend ?
                            R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                }
            });
        }
    }

    void showInfoByUser(User user) {
        mTvUsername.setText(username);
        mTvSex.setText(UserPropUtil.getSexByUser(user));
        mTvTel.setText(UserPropUtil.getTelByUser(user));
        mTvSign.setText(UserPropUtil.getSignByUser(user));
        mTvNikeName.setText(UserPropUtil.getNikeNameByUser(user));
        mTvBirthDay.setText(UserPropUtil.getBirthDayByUser(user));
        mTvEmail.setText(UserPropUtil.getEmailByUser(user));

        ImageLoader.getInstance().displayImage(user.getIconUrl(), mHeadIcon, DisplayImageOptionsUtil.OPTION_1);

 /*       ImageCacheUtil.showPicture(FriendInfoActivity.this, user.getIconUrl(), new ImageCacheUtil.CallBack() {
            @Override
            public void done(final Bitmap bitmap) {
                mHeadIcon.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeadIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });*/
    }

    void showInfoByAVUser(MyAVUser user) {
        mTvUsername.setText(username);
        mTvSex.setText(UserPropUtil.getSexByAVUser(user));
        mTvTel.setText(UserPropUtil.getTelByAVUser(user));
        mTvSign.setText(UserPropUtil.getSignByAVUser(user));
        mTvNikeName.setText(UserPropUtil.getNikeNameByAVUser(user));
        mTvBirthDay.setText(UserPropUtil.getBirthDayByAVUser(user));
        mTvEmail.setText(UserPropUtil.getEmailByAVUser(user));
        ImageLoader.getInstance().displayImage(user.getIcon().getUrl(), mHeadIcon, DisplayImageOptionsUtil.OPTION_1);
       /* ImageCacheUtil.showPicture(FriendInfoActivity.this, user.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
            @Override
            public void done(final Bitmap bitmap) {
                mHeadIcon.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeadIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });*/
    }

    void initStarIcon(final User user) {

        mStar.setIcon(0);
        isFriend =MyApplication.getDBHelper().isFriend(user.getUserName());
        mStar.setIcon(MyApplication.getDBHelper().isFriend(user.getUserName()) ?
                R.drawable.ic_action_important : R.drawable.ic_action_not_important);

/*        MyAVUser.followeeQuery(MyAVUser.getCurrentUser().getObjectId(), MyAVUser.class).whereEqualTo("followee", user)
                .findInBackground(new FindCallback<MyAVUser>() {
                    @Override
                    public void done(List<MyAVUser> list, AVException e) {
                        if (e == null) {
                            isFriend = !list.isEmpty();
                        } else {
                            e.printStackTrace();
                            isFriend = false;
                        }
                        mStar.setIcon(isFriend ?
                                R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                    }
                });*/


/*        MyAVUser.getCurrentUser().friendshipQuery().getInBackground(new AVFriendshipCallback() {
            @Override
            public void done(AVFriendship avFriendship, AVException e) {
                if (e == null) {
                    Log.d("sss", avFriendship.getFollowees().size() + "");
                    for (int i = 0; i < avFriendship.getFollowees().size() && !isFriend; i++) {
                        isFriend = ((AVUser) avFriendship.getFollowees().get(i)).getUsername().equals(user.getUserName());
                    }
                    mStar.setIcon(isFriend ?
                            R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                } else
                    e.printStackTrace();
            }
        });*/
    }

    private CircleImageView mHeadIcon;
    private FloatingActionButton mStar;
    private TextView mTvSign, mTvUsername, mTvBirthDay, mTvNikeName, mTvSex, mTvTel, mTvEmail;

    private boolean isFriend = false;
    private static final String EXTRA_USERNAME = "username";
    private String username;
    private User mFriend;

}
