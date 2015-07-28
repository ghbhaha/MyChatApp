package com.suda.mychatapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.utils.DateFmUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EditAccountActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        initWidget();
        //initEntity();
    }

    public void editSex(View view) {
        final CharSequence[] items = {"男", "女"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择性别");
        builder.setSingleChoiceItems(items, mSex, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int item) {
                UserBus.getMe(new UserBus.CallBack() {
                    @Override
                    public void done(MyAVUser user) {
                        user.setSex(items[item].toString());
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    mSex = item;
                                    mTvSex.setText(items[item].toString());
                                    dialog.cancel();
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
        builder.create().show();
    }

    public void editBirth(View view) {
        int year;
        int month;
        int day;
        if (birthday == null) {
            final Calendar d = Calendar.getInstance(Locale.CHINA);
            Date myDate = new Date();
            d.setTime(myDate);
            year = d.get(Calendar.YEAR);
            month = d.get(Calendar.MONTH);
            day = d.get(Calendar.DAY_OF_MONTH);
        } else {
            final Calendar d = Calendar.getInstance(Locale.CHINA);
            d.setTime(birthday);
            year = d.get(Calendar.YEAR);
            month = d.get(Calendar.MONTH);
            day = d.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final Date newdate = new Date(year - 1900, monthOfYear, dayOfMonth);
                UserBus.getMe(new UserBus.CallBack() {
                    @Override
                    public void done(MyAVUser user) {
                        user.setBirthDay(newdate);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    birthday = newdate;
                                    mTvBirth.setText(DateFmUtil.fmDateBirth(newdate));
                                }
                            }
                        });
                    }
                });
            }
        }, year, month, day);
        dlg.show();


/*
        CalendarDatePickerDialog dialog = CalendarDatePickerDialog.newInstance(new CalendarDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(final CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
                final Date newdate = new Date(year - 1900, month, day);
                UserBus.getMe(new UserBus.CallBack() {
                    @Override
                    public void done(MyAVUser user) {
                        user.setBirthDay(newdate);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    birthday = newdate;
                                    mTvBirth.setText(DateFmUtil.fmDateBirth(newdate));
                                }
                            }
                        });
                    }
                });
            }
        }, year, month, day);
        dialog.show(getSupportFragmentManager(), DATE_PICKER_TAG);
*/

    }


    public void editNikeName(View view) {
        Intent it = new Intent(EditAccountActivity.this, DetailEditActivity.class);
        it.putExtra("request_code", REQUEST_CODE_NIKENAME);
        startActivityForResult(it, REQUEST_CODE_NIKENAME);
    }

    public void editSign(View view) {
        Intent it = new Intent(EditAccountActivity.this, DetailEditActivity.class);
        it.putExtra("request_code", REQUEST_CODE_SIGN);
        startActivityForResult(it, REQUEST_CODE_SIGN);
    }

    public void editTel(View view) {
        Intent it = new Intent(EditAccountActivity.this, DetailEditActivity.class);
        it.putExtra("request_code", REQUEST_CODE_TEL);
        startActivityForResult(it, REQUEST_CODE_TEL);
    }

    public void editEmail(View view) {
        Intent it = new Intent(EditAccountActivity.this, DetailEditActivity.class);
        it.putExtra("request_code", REQUEST_CODE_EMAIL);
        startActivityForResult(it, REQUEST_CODE_EMAIL);
    }

    void initWidget() {
        mTvNikeName = (TextView) findViewById(R.id.tv_nikename);
        mTvSign = (TextView) findViewById(R.id.tv_sign);
        mTvSex = (TextView) findViewById(R.id.tv_sex);
        mTvBirth = (TextView) findViewById(R.id.tv_birth);
        mTvTel = (TextView) findViewById(R.id.tv_tel);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
    }

    void initEntity() {
        UserBus.getMe(new UserBus.CallBack() {
            @Override
            public void done(MyAVUser user) {
                mTvNikeName.setText(UserPropUtil.getNikeNameTipByAVUser(user));
                mTvSex.setText(UserPropUtil.getSexTipByAVUser(user));
                mTvSign.setText(UserPropUtil.getSignTipByAVUser(user));
                mTvTel.setText(UserPropUtil.getTelTipByAVUser(user));
                mTvEmail.setText(UserPropUtil.getEmailTipByAVUser(user));
                mTvBirth.setText(UserPropUtil.getBirthDayTipByAVUser(user));
                if (TextUtil.isTextEmpty(user.getSex())) {
                    mSex = -1;
                } else {
                    mSex = user.getSex().equals("男") ? 0 : 1;
                }

                birthday = user.getBirthDay();
            }
        });
    }

    @Override
    protected void onResume() {
        initEntity();
        super.onResume();
    }

    private final static int REQUEST_CODE_NIKENAME = 1;
    private final static int REQUEST_CODE_SIGN = 2;
    private final static int REQUEST_CODE_TEL = 3;
    private final static int REQUEST_CODE_EMAIL = 4;

    //private static final String DATE_PICKER_TAG = "DATE_PICKER_TAG";

    private TextView mTvNikeName, mTvSign, mTvSex, mTvBirth, mTvTel, mTvEmail;
    private int mSex = -1;
    private Date birthday = null;


}
