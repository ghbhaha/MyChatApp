package com.suda.mychatapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.GetDataCallback;
import com.suda.mychatapp.R;
import com.suda.mychatapp.business.UserBus;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.frg.NoAccountFrg;
import com.suda.mychatapp.util.CacheUtil;
import com.suda.mychatapp.util.DiskLruCache;
import com.suda.mychatapp.widget.PagerSlidingTabStrip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mHandler = new Handler();
        initWidget();
        initEntity();

    }


    private void initEntity() {
        MyAVUser user = (MyAVUser) MyAVUser.getCurrentUser();
        new UserBus().getMe(new UserBus.CallBack() {
            @Override
            public void done(MyAVUser me) {
/*                if (me.getIcon() != null) me.getIcon().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {

                        mHeadIcon.setImageBitmap(bitmap);
                    }
                });*/
                if(me.getIcon()!=null){
                    CacheUtil.showPicture(MainActivity.this, me.getIcon().getUrl(), new CacheUtil.CallBack() {
                        @Override
                        public void done(final Bitmap bitmap) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mHeadIcon.setImageBitmap(bitmap);
                                }
                            });

                        }
                    });
                }
                mTvusername.setText(me.getUsername());
            }
        });

    }

    private void initWidget() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
        View headerContainer = LayoutInflater.from(this).inflate(
                R.layout.siderbar_header, lvLeftMenu, false);

        mTvusername = (TextView) headerContainer.findViewById(R.id.tv_username);
        mHeadIcon = (CircleImageView) headerContainer.findViewById(R.id.profile_image);

        lvLeftMenu.addHeaderView(headerContainer);
        mlistItems = new ArrayList<HashMap<String, Object>>();
        addLeftMenu("a", R.mipmap.ic_launcher);
        mlistItemAdapter = new SimpleAdapter(this, mlistItems,
                R.layout.siderbar_lisetview_item, new String[]{"ItemTitle",
                "ItemImage"}, new int[]{R.id.ItemTitle, R.id.ItemImage});

        lvLeftMenu.setAdapter(mlistItemAdapter);


        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                // openOrClose = false;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                // openOrClose = true;
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        // colorChange(arg0);
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

            return new NoAccountFrg();

        }

    }

    private void addLeftMenu(String title, int res) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", title);
        map.put("ItemImage", res);
        mlistItems.add(map);
    }

    private TextView mTvusername;
    private CircleImageView mHeadIcon;
    private Handler mHandler;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ListView lvLeftMenu;
    private List<HashMap<String, Object>> mlistItems;
    private SimpleAdapter mlistItemAdapter;

}

