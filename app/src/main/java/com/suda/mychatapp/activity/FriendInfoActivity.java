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
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
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
        mTvsign = (TextView) findViewById(R.id.tv_sign);
        mTvusername = (TextView) findViewById(R.id.tv_username);
        mTvsex = (TextView) findViewById(R.id.tv_sex);
        mTvtel = (TextView) findViewById(R.id.tv_tel);
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
            UserBus.findUser(username, new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    showInfo(user);
                    initStarIcon(user);
                }
            });
        }
    }

    void showInfo(MyAVUser user) {
        mTvusername.setText(username);
        mTvsex.setText(UserPropUtil.getSex(user));
        mTvtel.setText(UserPropUtil.getTel(user));
        mTvsign.setText(UserPropUtil.getSign(user));
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

    void initStarIcon(final MyAVUser user) {

        Log.d("sss", user.getObjectId());

        mStar.setIcon(0);

        MyAVUser.followeeQuery(MyAVUser.getCurrentUser().getObjectId(), MyAVUser.class).whereEqualTo("followee", user)
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
                });


/*        MyAVUser.getCurrentUser().friendshipQuery().getInBackground(new AVFriendshipCallback() {
            @Override
            public void done(AVFriendship avFriendship, AVException e) {
                if (e == null) {
                    Log.d("sss", avFriendship.getFollowees().size() + "");
                    for (int i = 0; i < avFriendship.getFollowees().size() && !isFriend; i++) {
                        isFriend = ((AVUser) avFriendship.getFollowees().get(i)).getUsername().equals(user.getUsername());
                    }
                    mStar.setIcon(isFriend ?
                            R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                } else
                    e.printStackTrace();
            }
        });*/
    }

    private CircleImageView mHeadIcon;
    private TextView mTvsign;
    private TextView mTvusername;
    private TextView mTvsex;
    private TextView mTvtel;
    private FloatingActionButton mStar;


    private boolean isFriend = false;
    private static final String EXTRA_USERNAME = "username";
    private String username;

}
