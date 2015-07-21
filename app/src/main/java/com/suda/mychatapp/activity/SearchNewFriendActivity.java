package com.suda.mychatapp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.util.CacheUtil;
import com.suda.mychatapp.util.TextUtil;

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
                        mTvsign.setText(mFriendUser.getSign());
                        mTvnikeName.setText(mFriendUser.getUsername());
                        if(mFriendUser.getIcon()!=null){
                            CacheUtil.showPicture(SearchNewFriendActivity.this, mFriendUser.getIcon().getUrl(), new CacheUtil.CallBack() {
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
                        e.printStackTrace();
                    }
                }
            });


        }

    }

    public void addUserToMine(MyAVUser friend){

        friend.friendshipQuery();

    }

    void initWidget() {
        mEtfriendUsername = (EditText) findViewById(R.id.et_friend_username);
        mRlFriend = (RelativeLayout) findViewById(R.id.new_friends_ll);
        mTvnikeName=(TextView)findViewById(R.id.tv_nikename);
        mTvsign=(TextView)findViewById(R.id.tv_sign);
        mHeadIcon=(CircleImageView)findViewById(R.id.icon);
    }

    private MyAVUser mFriendUser;
    private RelativeLayout mRlFriend;
    public TextView mTvnikeName;
    public TextView mTvsign;
    public CircleImageView mHeadIcon;
    private EditText mEtfriendUsername;

}
