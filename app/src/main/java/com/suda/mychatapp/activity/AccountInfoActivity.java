package com.suda.mychatapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.suda.mychatapp.AbstructActivity;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.util.CacheUtil;
import com.suda.mychatapp.util.TextUtil;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountInfoActivity extends AbstructActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        initWidget();
        initEntity();
    }

    private void initEntity() {
        if (UserBus.isExistLocalUser()) {
            new UserBus().getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser me) {
                    mTvsign.setText(TextUtil.isTextEmpty(me.getSign())?"请填写签名哦":"“" + me.getSign() + "”");
                    mTvusername.setText(me.getUsername());
                    mTvsex.setText(TextUtil.isTextEmpty(me.getSex()) ? "--" : me.getSex());
                    mTvtel.setText(TextUtil.isTextEmpty(me.getMobilePhoneNumber()) ? "--" : me.getMobilePhoneNumber());
                    if (me.getIcon() != null) {
                        CacheUtil.showPicture(AccountInfoActivity.this, me.getIcon().getUrl(), new CacheUtil.CallBack() {
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
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null)
                        cropPhoto(data.getData());
                }
                break;
            case 2:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    mHeadBitMap = extras.getParcelable("data");
                    if (mHeadBitMap != null) {
                        final AVFile f = new AVFile("icon", Bitmap2Bytes(mHeadBitMap));
                        UserBus.getMe(new UserBus.CallBack() {
                            @Override
                            public void done(MyAVUser me) {
                                me.put("head_icon", f);
                                me.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null)
                                            UserBus.refreshMe(new UserBus.CallBack() {
                                                @Override
                                                public void done(MyAVUser me) {
                                                    CacheUtil.showPicture(AccountInfoActivity.this, me.getIcon().getUrl(), new CacheUtil.CallBack() {
                                                        @Override
                                                        public void done(Bitmap bitmap) {
                                                            //do nothing
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
                                            });
                                    }
                                });
                            }
                        });
                    }
                }
                break;
            case 3:
                break;

            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    public void setHeadIcon(View view) {
        Intent it = new Intent(Intent.ACTION_PICK, null);
        it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(it, 1);
    }

    public void editAccountInfo(View view) {
        Intent it = new Intent(this, EditAccountActivity.class);
        startActivityForResult(it, 3);
    }

    public void logout(View v) {
        AVUser.getCurrentUser().logOut();
        this.finish();
    }

    public void setSign(View view) {
        final EditText et = new EditText(this);
        et.setText(mTvsign.getText().toString().replace("“", "").replace("”", ""));
        new AlertDialog.Builder(this).setTitle("请填写签名").setView(et).setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtil.isTextEmpty(et.getText().toString())) {
                    UserBus.getMe(new UserBus.CallBack() {
                        @Override
                        public void done(MyAVUser me) {
                            me.setSign(et.getText().toString());
                            me.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null)
                                        mTvsign.setText("“" + et.getText().toString() + "”");
                                    else
                                        e.printStackTrace();
                                }
                            });
                        }
                    });

                } else
                    return;
            }
        }).setNegativeButton("算了", null).show();
    }

    void initWidget() {
        mHeadIcon = (CircleImageView) findViewById(R.id.profile_image);
        mTvsign = (TextView) findViewById(R.id.tv_sign);
        mTvusername = (TextView) findViewById(R.id.tv_username);
        mTvsex = (TextView) findViewById(R.id.tv_sex);
        mTvtel = (TextView) findViewById(R.id.tv_tel);
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private CircleImageView mHeadIcon;
    private TextView mTvsign;
    private TextView mTvusername;
    private TextView mTvsex;
    private TextView mTvtel;
    private static String path = "/sdcard/myHead/";
    private Bitmap mHeadBitMap;
}
