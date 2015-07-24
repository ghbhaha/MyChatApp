package com.suda.mychatapp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.ExceptionInfoUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;

public class DetailEditActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);
        initWidget();
    }


    public void confirm(View view) {
        final String content = mEtContent.getText().toString();
        if (TextUtil.isTextEmpty(content)) {
            return;
        }
        if (mRequestcode == REQUEST_CODE_NIKENAME) {
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    user.setNikename(content);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null)
                                Toast.makeText(DetailEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            DetailEditActivity.this.finish();
                        }
                    });
                }
            });
        }

        if (mRequestcode == REQUEST_CODE_SIGN) {
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    user.setSign(content);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null)
                                Toast.makeText(DetailEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            DetailEditActivity.this.finish();
                        }
                    });
                }
            });
        }

        if (mRequestcode == REQUEST_CODE_TEL) {
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(final MyAVUser user) {
                    final String tmp;
                    tmp = user.getMobilePhoneNumber();
                    user.setMobilePhoneNumber(content);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                Toast.makeText(DetailEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                DetailEditActivity.this.finish();
                            } else {
                                ExceptionInfoUtil.toastError(DetailEditActivity.this, e.getCode());
                                user.setMobilePhoneNumber(tmp);
                            }
                        }
                    });
                }
            });
        }

        if (mRequestcode == REQUEST_CODE_EMAIL) {
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(final MyAVUser user) {
                    final String tmp;
                    tmp = user.getEmail();
                    user.setEmail(content);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                Toast.makeText(DetailEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                DetailEditActivity.this.finish();
                            } else {
                                user.setEmail(tmp);
                                ExceptionInfoUtil.toastError(DetailEditActivity.this,e.getCode());
                            }
                        }
                    });
                }
            });
        }
    }


    void initWidget() {
        mEtContent = (EditText) findViewById(R.id.content);
        mRequestcode = getIntent().getIntExtra("request_code", 0);
        if (REQUEST_CODE_NIKENAME == mRequestcode) {
            getSupportActionBar().setTitle("修改昵称");
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    if (TextUtil.isTextEmpty(user.getNikename())) {
                        mEtContent.setHint("请填写昵称");
                    } else {
                        mEtContent.setText(user.getNikename());
                    }

                }
            });
        }
        if (REQUEST_CODE_SIGN == mRequestcode) {
            getSupportActionBar().setTitle("修改签名");
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    if (TextUtil.isTextEmpty(user.getSign())) {
                        mEtContent.setHint("请填写签名");
                    } else {
                        mEtContent.setText(user.getSign());
                    }
                }
            });
        }
        if (REQUEST_CODE_TEL == mRequestcode) {
            getSupportActionBar().setTitle("修改电话");
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    if (TextUtil.isTextEmpty(user.getMobilePhoneNumber())) {
                        mEtContent.setHint("请填写电话号码");
                    } else {
                        mEtContent.setText(user.getMobilePhoneNumber());
                    }
                }
            });
        }
        if (REQUEST_CODE_EMAIL == mRequestcode) {
            getSupportActionBar().setTitle("修改邮箱");
            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser user) {
                    if (TextUtil.isTextEmpty(user.getEmail())) {
                        mEtContent.setHint("请填写邮箱");
                    } else {
                        mEtContent.setText(user.getEmail());
                    }
                }
            });
        }
    }

    private final static int REQUEST_CODE_NIKENAME = 1;
    private final static int REQUEST_CODE_SIGN = 2;
    private final static int REQUEST_CODE_TEL = 3;
    private final static int REQUEST_CODE_EMAIL = 4;


    private int mRequestcode;

    private EditText mEtContent;

}
