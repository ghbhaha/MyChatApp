package com.suda.mychatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.avos.avoscloud.FollowCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.ImageCacheUtil;
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
                        mFriendUser = list.get(0);
                        mRlFriend.setVisibility(View.VISIBLE);
                        mTvsign.setText(UserPropUtil.getSign(mFriendUser));
                        mTvnikeName.setText(UserPropUtil.getNikeName(mFriendUser));
                        if (mFriendUser.getIcon() != null) {
                            ImageCacheUtil.showPicture(SearchNewFriendActivity.this, mFriendUser.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
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
                            });
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
        Log.d("obj",mFriendUser.getObjectId());
        MyAVUser.getCurrentUser().followInBackground(mFriendUser.getObjectId(), new FollowCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e==null){
                    Toast.makeText(SearchNewFriendActivity.this,"添加成功，返回刷新一下吧",Toast.LENGTH_SHORT).show();
                }else{
                    e.printStackTrace();
                }
            }

            @Override
            protected void internalDone0(Object o, AVException e) {
                if (e==null){
                    Toast.makeText(SearchNewFriendActivity.this,"添加成功，返回刷新一下吧",Toast.LENGTH_SHORT).show();
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    public void getFriendInfo(View view){
        Intent it = new Intent(SearchNewFriendActivity.this, FriendInfoActivity.class);
        it.putExtra(EXTRA_USERNAME, mFriendUser.getUsername());
        startActivity(it);
    }

    void initWidget() {
        mEtfriendUsername = (EditText) findViewById(R.id.et_friend_username);
        mRlFriend = (RelativeLayout) findViewById(R.id.new_friends_ll);
        mTvnikeName = (TextView) findViewById(R.id.tv_nikename);
        mTvsign = (TextView) findViewById(R.id.tv_sign);
        mHeadIcon = (CircleImageView) findViewById(R.id.icon);
    }

    private static MyAVUser mFriendUser;
    private RelativeLayout mRlFriend;
    public TextView mTvnikeName;
    public TextView mTvsign;
    public CircleImageView mHeadIcon;
    private EditText mEtfriendUsername;
    private static final String EXTRA_USERNAME = "username";


}
