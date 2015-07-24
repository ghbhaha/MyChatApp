package com.suda.mychatapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.ImageUtil;
import com.suda.mychatapp.utils.TextUtil;

public class RegisterActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initWidget();
    }


    public void register(View view) {
        String username = mEtusername.getText().toString();
        String password = mEtpassword.getText().toString();
        String password2 = mEtpassword2.getText().toString();

        mCanDo = true;

        if (TextUtil.isTextEmpty(username)) {
            mTvtipusername.setVisibility(View.VISIBLE);
            mTvtipusername.startAnimation(mShakeAnim);
            mCanDo = false;
        }

        if (TextUtil.isTextEmpty(password)) {
            mTvtippassword.setVisibility(View.VISIBLE);
            mTvtippassword.startAnimation(mShakeAnim);
            mCanDo = false;
        }

        if (TextUtil.isTextEmpty(password2)) {
            mTvtippassword2.setVisibility(View.VISIBLE);
            mTvtippassword2.startAnimation(mShakeAnim);
            mCanDo = false;
        }

        Log.d("test2", mCanDo + "");

        if (mCanDo && !password.equals(password2)) {
            Toast.makeText(this, getString(R.string.password_not_same), Toast.LENGTH_SHORT).show();
            mCanDo = false;
        }

        if (mCanDo) {
            final MyAVUser user = new MyAVUser();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            AVFile avFile = new AVFile("icon", ImageUtil.Bitmap2Bytes(bitmap));
            user.setIcon(avFile);
            user.setUsername(username);
            user.setPassword(password);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.regist_success), Toast.LENGTH_SHORT).show();
                        user.logOut();
                        RegisterActivity.this.finish();
                    } else {
                        if (e.getCode() == AVException.USERNAME_TAKEN) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.username_taken), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    }


    private void initWidget() {
        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

        mEtusername = (EditText) findViewById(R.id.et_username);
        mEtpassword = (EditText) findViewById(R.id.et_password);
        mEtpassword2 = (EditText) findViewById(R.id.et_password2);
        mTvtipusername = (TextView) findViewById(R.id.tip_username);
        mTvtippassword = (TextView) findViewById(R.id.tip_password);
        mTvtippassword2 = (TextView) findViewById(R.id.tip_password2);

        mEtusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCanDo = true;
                mTvtipusername.setVisibility(View.INVISIBLE);
            }
        });

        mEtpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCanDo = true;
                mTvtippassword.setVisibility(View.INVISIBLE);
            }
        });

        mEtpassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCanDo = true;
                mTvtippassword2.setVisibility(View.INVISIBLE);
            }
        });

    }

    private EditText mEtusername;
    private EditText mEtpassword;
    private EditText mEtpassword2;
    private TextView mTvtipusername;
    private TextView mTvtippassword;
    private TextView mTvtippassword2;

    private boolean mCanDo = true;
    private Animation mShakeAnim;

}
