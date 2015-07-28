package com.suda.mychatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.ExceptionInfoUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UIUtil;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UIUtil.setNullElevation(this);
        UIUtil.setActionBarBack(this);

        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
        checkIfLogin();
        initWidget();

    }

    public void login(View v) {
        mCanDo = true;

        final String username = mEtUsername.getText().toString();
        final String password = mEtPassword.getText().toString();

        if (TextUtil.isTextEmpty(username)) {
            mTvTipUsername.setVisibility(View.VISIBLE);
            mTvTipUsername.startAnimation(mShakeAnim);
            mCanDo = false;
        }
        if (TextUtil.isTextEmpty(password)) {
            mTvTipPassword.setVisibility(View.VISIBLE);
            mTvTipPassword.startAnimation(mShakeAnim);
            mCanDo = false;
        }

        if (mCanDo) {
            AVUser.logInInBackground(username, password, new LogInCallback<MyAVUser>() {
                @Override
                public void done(MyAVUser avUser, AVException e) {
                    if (e == null) {
                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(it);
                        LoginActivity.this.finish();
                    } else {
                        ExceptionInfoUtil.toastError(LoginActivity.this, e.getCode());
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
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mTvTipUsername = (TextView) findViewById(R.id.tip_username);
        mTvTipPassword = (TextView) findViewById(R.id.tip_password);
        mTvRegister = (TextView) findViewById(R.id.regist);

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvTipUsername.setVisibility(View.INVISIBLE);
                mCanDo = true;
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvTipPassword.setVisibility(View.INVISIBLE);
                mCanDo = true;
            }
        });

        mTvRegister.setText(Html.fromHtml("<u>" + getString(R.string.regist_new) + "</u>"));

    }

    private void checkIfLogin() {
        MyAVUser user = (MyAVUser) MyAVUser.getCurrentUser();
        if (user != null) {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private EditText mEtUsername, mEtPassword;
    private TextView mTvRegister, mTvTipUsername, mTvTipPassword;


    private boolean mCanDo = true;
    private Animation mShakeAnim;


}
