package com.suda.mychatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suda.mychatapp.Conf;
import com.suda.mychatapp.MyApplication;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.fragment.ConversationFrg;
import com.suda.mychatapp.fragment.DongTaiFrg;
import com.suda.mychatapp.fragment.FriendsFrg;
import com.suda.mychatapp.utils.DisplayImageOptionsUtil;
import com.suda.mychatapp.utils.DoubleClickExitHelper;
import com.suda.mychatapp.utils.NotificationUtil;
import com.suda.mychatapp.utils.TextUtil;
import com.suda.mychatapp.utils.UserPropUtil;
import com.suda.mychatapp.utils.msg.MessageHandler;
import com.suda.mychatapp.utils.msg.MsgIFace;
import com.suda.mychatapp.widget.PagerSlidingTabStrip;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initWidget();

        msgIFace = new MsgIFace() {
            @Override
            public void update() {
                updateUnreadCount();
            }

            @Override
            public void update(AVIMTextMessage message) {

            }
        };

        MessageHandler.setiFace2(msgIFace);
    }


    private void updateUnreadCount(){
        tvUnreadCount.setVisibility(MyApplication.mDbHelper.getAllUnReadCount() == 0 ? View.INVISIBLE : View.VISIBLE);
        tvUnreadCount.setText(MyApplication.mDbHelper.getAllUnReadCount() + "");
    }

    private void initEntity() {

        if (UserBus.isExistLocalUser()) {

            UserBus.getMe(new UserBus.CallBack() {
                @Override
                public void done(MyAVUser me) {
                    openClient(me.getUsername());
                    mTvNikeName.setText(UserPropUtil.getNikeNameByAVUser(me));
                    mTvSign.setVisibility(TextUtil.isTextEmpty(me.getSign()) ? View.GONE : View.VISIBLE);
                    mTvSign.setText("“" + me.getSign() + "”");

                    if (me.getIcon() != null) {
                        ImageLoader.getInstance().displayImage(me.getIcon().getUrl(), mHeadIcon, DisplayImageOptionsUtil.OPTION_1);
                        /*ImageCacheUtil.showPicture(MainActivity.this, me.getIcon().getUrl(), new ImageCacheUtil.CallBack() {
                            @Override
                            public void done(final Bitmap bitmap) {
                                final Bitmap bm = bitmap;
                                mHeadIcon.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mHeadIcon.setImageBitmap(bm);
                                    }
                                });
                            }
                        });*/
                    }
                }
            });
        } else {
            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(it);
            this.finish();
        }
    }


    private void initWidget() {

        mClickExitHelper = new DoubleClickExitHelper(MainActivity.this);

        tvUnreadCount = (TextView) findViewById(R.id.tv_unreadCount);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
        View headerContainer = LayoutInflater.from(this).inflate(
                R.layout.siderbar_header, lvLeftMenu, false);

        mTvNikeName = (TextView) headerContainer.findViewById(R.id.tv_username);
        mTvSign = (TextView) headerContainer.findViewById(R.id.tv_sign);
        mHeadIcon = (CircleImageView) headerContainer.findViewById(R.id.profile_image);

        lvLeftMenu.addHeaderView(headerContainer);
        mListItems = new ArrayList<HashMap<String, Object>>();
        addLeftMenu("Suda聊天室（实验）", R.drawable.ic_drawer_settings);
        addLeftMenu("检测更新", R.drawable.browser_refresh);
        addLeftMenu("设置", R.drawable.ic_drawer_settings);
        addLeftMenu("帮助", R.drawable.ic_drawer_about);
        addLeftMenu("退出", R.drawable.ic_drawer_exit);
        mListItemAdapter = new SimpleAdapter(this, mListItems,
                R.layout.siderbar_lisetview_item, new String[]{"ItemTitle",
                "ItemImage"}, new int[]{R.id.ItemTitle, R.id.ItemImage});

        lvLeftMenu.setAdapter(mListItemAdapter);

        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent it = new Intent(MainActivity.this, AccountInfoActivity.class);
                        startActivityForResult(it, REQUEST_UPDATE_IMAGE);
                        break;
                    case 1:
                        AVIMConversation conversation = MyApplication.getIMClient().getConversation(Conf.GROUP_CONVERSATION_ID);
                        if (conversation.getMembers().contains(MyAVUser.getCurrentUser().getUsername())) {
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra(EXTRA_CONVERSATION_ID, Conf.GROUP_CONVERSATION_ID);
                            intent.putExtra(EXTRA_USERNAME, "Suda聊天室");
                            startActivity(intent);
                        } else {
                            conversation.join(new AVIMConversationCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Intent it = new Intent(MainActivity.this, ChatActivity.class);
                                        it.putExtra(EXTRA_CONVERSATION_ID, Conf.GROUP_CONVERSATION_ID);
                                        it.putExtra(EXTRA_USERNAME, "Suda聊天室");
                                        startActivity(it);
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        break;
                    case 2:
                        checkForUpdate();
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case 4:
                        break;
                    case 5:
                        MainActivity.this.finish();
                        break;
                }
            }
        });


        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                openOrClose = false;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                openOrClose = true;
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.setCurrentItem(1);

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {

                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                    }
                });
        initTabsValue();
    }

    private void initTabsValue() {
        mPagerSlidingTabStrip.setIndicatorColor(Color.WHITE);
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#6699FF"));
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources()
                        .getDisplayMetrics()));
        mPagerSlidingTabStrip.setShouldExpand(true);
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources()
                        .getDisplayMetrics()));
        mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
        mPagerSlidingTabStrip.setTextColor(Color.BLACK);
    }

    public void checkForUpdate() {
        UmengUpdateAgent.setDeltaUpdate(true);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                switch (i) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateResponse);
                        break;
                    case UpdateStatus.No: // has no update
                        Toast.makeText(MainActivity.this, "没有检测到更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // time out
                        Toast.makeText(MainActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        UmengUpdateAgent.forceUpdate(MainActivity.this);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = getResources().getStringArray(
                R.array.tab_title);

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ConversationFrg().setViewPager(mViewPager);
                case 1:
                    return new FriendsFrg();
                case 2:
                    return new DongTaiFrg();
                default:
                    return null;
            }
        }

    }

    public void searchNewFriend(View view) {
        Intent it = new Intent(this, SearchNewFriendActivity.class);
        startActivity(it);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_UPDATE_IMAGE) {
                Bundle extras = data.getExtras();
                Bitmap bm = extras.getParcelable("data");
                mHeadIcon.setImageBitmap(bm);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (MyAVUser.getCurrentUser() != null) {
            AVIMClient.getInstance(MyAVUser.getCurrentUser().getUsername()).close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVException e) {
                    if (e != null)
                        e.printStackTrace();
                }
            });
        }
        AVIMMessageManager.unregisterMessageHandler(AVIMTypedMessage.class, MyApplication.mMessageHandler);
        NotificationUtil.clearNotification(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        NotificationUtil.createNotification(this);
        MessageHandler.getiFace2().update();
        super.onPause();
    }

    @Override
    protected void onResume() {
        initEntity();
        NotificationUtil.clearNotification(this);
        MessageHandler.getiFace2().update();
        super.onResume();
    }

    public void openClient(String selfId) {
        AVIMClient imClient = AVIMClient.getInstance(selfId);
        imClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (openOrClose == false) {
                moveTaskToBack(false);
                // return mClickExitHelper.onKeyDown(keyCode, event);
            } else {
                mDrawerLayout.closeDrawers();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void addLeftMenu(String title, int res) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", title);
        map.put("ItemImage", res);
        mListItems.add(map);
    }

    private TextView mTvNikeName;
    private TextView mTvSign;
    private CircleImageView mHeadIcon;

    private boolean openOrClose = false;
    DoubleClickExitHelper mClickExitHelper;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ListView lvLeftMenu;
    private List<HashMap<String, Object>> mListItems;
    private SimpleAdapter mListItemAdapter;
    private static final int REQUEST_UPDATE_IMAGE = 1;

    private static final String EXTRA_USERNAME = "username";
    private static final String EXTRA_CONVERSATION_ID = "conversation_id";

    private MsgIFace msgIFace;
    private TextView tvUnreadCount;

}

