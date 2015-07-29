package com.suda.mychatapp;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.suda.mychatapp.business.LoadLib;
import com.suda.mychatapp.business.pojo.MyAVUser;
import com.suda.mychatapp.db.DbHelper;
import com.suda.mychatapp.iface.FriendsIFace;
import com.suda.mychatapp.utils.msg.MessageHandler;

import java.io.File;

/**
 * Created by Suda on 2015/7/18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
        LoadLib.LoadLib();

        mDbHelper = new DbHelper(this);

        mMessageHandler = new MessageHandler(this);

        initImageLoader(this);

        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, mMessageHandler);

    }

    public void initImageLoader(Context ct) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(ct, "MyChatApp/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ct)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大长宽
                .threadPoolSize(3) // default  线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default 可以自定义缓存路径
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
                        // .diskCacheFileCount(100)  // 可以缓存的文件数量
                        // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new BaseImageDownloader(ct)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs() // 打印debug log
                .build(); //开始构建
        ImageLoader.getInstance().init(config);

    }

    public static AVIMClient getIMClient() {
        return AVIMClient.getInstance(MyAVUser.getCurrentUser().getUsername());
    }

    public static void setFriendsIface(FriendsIFace mFriendsIFace) {
        MyApplication.mFriendsIFace = mFriendsIFace;
    }

    public static FriendsIFace getmFriendsIFace() {
        return mFriendsIFace;
    }

    public static DbHelper getDBHelper() {
        return mDbHelper;
    }

    public static DbHelper mDbHelper;

    private final static String APP_ID = "bbi2udim376ydh5lvhq6jzp4o2afosu9nndydes45jvolhj4";
    private final static String APP_KEY = "flbtai7ocvvvrsutun5k77jkgagvayew944mnms8e94u3z6j";
    public static FriendsIFace mFriendsIFace;
    public static MessageHandler mMessageHandler;


}
