package com.suda.mychatapp.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Suda on 2015/7/19.
 */
public class CacheUtil {

    public static void showPicture(Context context, String uniqueName, CallBack callBack) {
        if (mMemoryCache == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }

        if (mDiskLruCache == null) {
            try {
                // 获取图片缓存路径
                File cacheDir = getDiskCacheDir(context, "thumb");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                // 创建DiskLruCache实例，初始化缓存数据
                mDiskLruCache = DiskLruCache
                        .open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadBitmaps(callBack, uniqueName);

    }

    public static void loadBitmaps(final CallBack callBack, final String imageUrl) {
        try {
            Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
            if (bitmap == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileDescriptor fileDescriptor = null;
                        FileInputStream fileInputStream = null;
                        DiskLruCache.Snapshot snapShot = null;
                        try {
                            // 生成图片URL对应的key
                            final String key = hashKeyForDisk(imageUrl);
                            // 查找key对应的缓存
                            snapShot = mDiskLruCache.get(key);

                            boolean s = snapShot == null;

                            if (snapShot == null) {
                                // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                                if (editor != null) {
                                    OutputStream outputStream = editor.newOutputStream(0);
                                    if (downloadUrlToStream(imageUrl, outputStream)) {
                                        editor.commit();
                                    } else {
                                        editor.abort();
                                    }
                                }
                                // 缓存被写入后，再次查找key对应的缓存
                                snapShot = mDiskLruCache.get(key);
                                flushCache();
                            }
                            if (snapShot != null) {
                                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                                fileDescriptor = fileInputStream.getFD();
                            }
                            // 将缓存数据解析成Bitmap对象
                            Bitmap bitmap = null;
                            if (fileDescriptor != null) {
                                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                            }
                            if (bitmap != null) {
                                // 将Bitmap对象添加到内存缓存当中
                                callBack.done(bitmap);
                                addBitmapToMemoryCache(imageUrl, bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fileDescriptor == null && fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                }).start();

            } else {
                callBack.done(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static DiskLruCache getDiskLruCache(Context context, String uniqueName) {

        DiskLruCache mDiskLruCache = null;
        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            if (cacheDir.exists()) {
            } else {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mDiskLruCache;
    }


    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static void flushCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public interface CallBack {
        void done(Bitmap bitmap);
    }

    private static LruCache<String, Bitmap> mMemoryCache;
    private static DiskLruCache mDiskLruCache;

}
