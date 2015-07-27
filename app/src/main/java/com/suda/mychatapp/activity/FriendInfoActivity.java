package com.suda.mychatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFriendship;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.callback.AVFriendshipCallback;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.FriendsBus;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.pojo.User;
import com.suda.mychatapp.utils.ImageCacheUtil;
import com.suda.mychatapp.utils.UserPropUtil;

import java.util.List;

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
                    showInfo(user);
                }
            });

        } else {
            UserBus.findUser(username, new UserBus.CallBack2() {
                @Override
                public void done(User user) {
                    mFriend = user;
                    showInfo2(user);
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
                    isFriend = rs;
                    mStar.setIcon(isFriend ?
                            R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                }
            });
        }
    }

    void showInfo2(User user) {
        mTvUsername.setText(username);
        mTvSex.setText(UserPropUtil.getSex2(user));
        mTvTel.setText(UserPropUtil.getTel2(user));
        mTvSign.setText(UserPropUtil.getSign2(user));
        mTvNikeName.setText(UserPropUtil.getNikeName2(user));
        mTvBirthDay.setText(UserPropUtil.getBirthDay2(user));
        mTvEmail.setText(UserPropUtil.getEmail2(user));

        ImageCacheUtil.showPicture(FriendInfoActivity.this, user.getIconUrl(), new ImageCacheUtil.CallBack() {
            @Override
            public void done(final Bitmap bitmap) {
                mHeadIcon.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeadIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    void showInfo(MyAVUser user) {
        mTvUsername.setText(username);
        mTvSex.setText(UserPropUtil.getSex(user));
        mTvTel.setText(UserPropUtil.getTel(user));
        mTvSign.setText(UserPropUtil.getSign(user));
        mTvNikeName.setText(UserPropUtil.getNikeName(user));
        mTvBirthDay.setText(UserPropUtil.getBirthDay(user));
        mTvEmail.setText(UserPropUtil.getEmail(user));

        ImageCacheUtil.showPicture(FriendInfoActivity.this, user.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
            @Override
            public void done(final Bitmap bitmap) {
                mHeadIcon.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeadIcon.setImageBitmap(bitmap);
                    }
                });
            }
        });
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
    private TextView mTvSign;
    private TextView mTvUsername;
    private TextView mTvBirthDay;
    private TextView mTvNikeName;
    private TextView mTvSex;
    private TextView mTvTel;
    private TextView mTvEmail;
    private FloatingActionButton mStar;


    private boolean isFriend = false;
    private static final String EXTRA_USERNAME = "username";
    private String username;
    private User mFriend;

}
