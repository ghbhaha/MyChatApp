package com.suda.mychatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.util.TextUtil;

public class LoginActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
        checkIfLogin();
        initWidget();

    }

    public void login(View v) {
        mCanDo = true;

        final String username = mEtusername.getText().toString();
        final String password = mEtpassword.getText().toString();

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

        if (mCanDo) {
            AVUser.logInInBackground(username, password, new LogInCallback<MyAVUser>() {
                @Override
                public void done(MyAVUser avUser, AVException e) {
                    if (e == null) {
                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(it);
                    } else {
                        if (e.getCode() == AVException.USERNAME_PASSWORD_MISMATCH)
                            Toast.makeText(LoginActivity.this, getString(R.string.username_password_mismatch), Toast.LENGTH_SHORT).show();
                        else if (e.getCode() == AVException.USER_DOESNOT_EXIST)
                            Toast.makeText(LoginActivity.this, getString(R.string.user_dosenot_exist), Toast.LENGTH_SHORT).show();
                    }

                }
            }, MyAVUser.class);
        }
    }

    public void registerNew(View v) {
        Intent it = new Intent(this, RegisterActivity.class);
        startActivity(it);
    }

    private void initWidget() {
        mEtusername = (EditText) findViewById(R.id.et_username);
        mEtpassword = (EditText) findViewById(R.id.et_password);
        mTvtipusername = (TextView) findViewById(R.id.tip_username);
        mTvtippassword = (TextView) findViewById(R.id.tip_password);
        mTvregist = (TextView) findViewById(R.id.regist);

        mEtusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvtipusername.setVisibility(View.INVISIBLE);
                mCanDo = true;
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
                mTvtippassword.setVisibility(View.INVISIBLE);
                mCanDo = true;
            }
        });

        mTvregist.setText(Html.fromHtml("<u>" + getString(R.string.regist_new) + "</u>"));

    }

    private void checkIfLogin() {
        MyAVUser user = (MyAVUser) MyAVUser.getCurrentUser();
        if (user != null) {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            this.finish();
        }

    }


    private EditText mEtusername;
    private EditText mEtpassword;
    private TextView mTvtipusername;
    private TextView mTvtippassword;
    private TextView mTvregist;


    private boolean mCanDo = true;
    private Animation mShakeAnim;
}
