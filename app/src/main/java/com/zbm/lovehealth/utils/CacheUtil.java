package com.zbm.lovehealth.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;


import com.jakewharton.disklrucache.DiskLruCache;
import com.zbm.lovehealth.AbstractDataBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 * Created by Administrator on 2018/4/22.
 */

public class CacheUtil {

    @SuppressLint("StaticFieldLeak")
    private static CacheUtil cacheUtil = null;  //待检测是否内存泄漏
    /**
     * 数据缓存技术的核心类，用于缓存所有下载好的数据，在程序内存达到设定值时会将最少最近使用的数据移除掉。
     * key为以String的哈希码来标识
     * <p>
     * 缓存机制：
     * 1.app无网状态下，由代理类先访问内存，没有再访问磁盘
     * 2.app有网状态下，由代理类访问网络
     * <p>
     * 每次访问完网络，会将数据分别缓存进内存和磁盘，并记录获取到的bean数到SharedPreference，以便作为读取缓存时的结束标志；
     * 对象数据缓存和图片数据缓存分开操作
     */
    private LruCache<String, AbstractDataBean> mMemoryCache;
    private LruCache<String, byte[]> mImageCache;
    private DiskLruCache mDiskCache;
    private Context context;

    private CacheUtil(Context context) {
        this.context = context;
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int objectCacheSize = maxMemory / 16;
        int imageCacheSize = maxMemory / 8;
        Log.d("CacheUtil", objectCacheSize + " " + imageCacheSize);
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, AbstractDataBean>(objectCacheSize) {
            @Override
            protected int sizeOf(String key, AbstractDataBean values) {
                return values.getObjectSize();
            }
        };

        mImageCache = new LruCache<String, byte[]>(imageCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length;
            }
        };
        try {
            //磁盘缓存大小为15M
            mDiskCache = DiskLruCache.open(getDiskCacheDir(context.getApplicationContext()),
                    getAppVersion(context.getApplicationContext()), 1, 15 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 该方法只在Application的onCreate中调用
     *
     * @param context ApplicationContext
     */
    public static void initCache(Context context) {
        cacheUtil = new CacheUtil(context);
    }

    public static CacheUtil getCacheUtilInstance() {
        return cacheUtil;
    }

    //把数据添加进缓存(同时内存+磁盘)
    public void addObjectToCache(String key, AbstractDataBean values) {
        key = String.valueOf(key.hashCode());
        mMemoryCache.put(key, values);
        try {
            DiskLruCache.Editor editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (objectDataToStream(values, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
            mDiskCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //把图片添加进缓存(同时内存+磁盘)
    public void addImageToCache(String key, Drawable drawable) {
        key = String.valueOf(key.hashCode());
        mImageCache.put(key, drawableToByte(drawable));
        try {
            DiskLruCache.Editor editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (drawableToStream(drawableToByte(drawable), outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
            mDiskCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将对象写入流
    private boolean objectDataToStream(AbstractDataBean dataBean, OutputStream outputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(dataBean);
            outputStream.write(byteArrayOutputStream.toByteArray());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //从流中读对象
    private AbstractDataBean objectDataFromStream(InputStream inputStream) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (AbstractDataBean) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //将图片写入流
    private boolean drawableToStream(byte[] bytes, OutputStream outputStream) {
        try {
            outputStream.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AbstractDataBean getObjectFromMemory(String key) {
        key = String.valueOf(key.hashCode());
        return mMemoryCache.get(key);
    }

    public AbstractDataBean getObjectFromDisk(String key) {
        key = String.valueOf(key.hashCode());
        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
            if (snapshot != null)
                return objectDataFromStream(snapshot.getInputStream(0));
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Drawable getImageFromMemory(String key) {
        key = String.valueOf(key.hashCode());
        byte[] bytes = mImageCache.get(key);
        if (bytes == null)
            return null;
        else
            return byteToDrawable(bytes);
    }

    public Drawable getImageFromDisk(String key) {
        key = String.valueOf(key.hashCode());
        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
            if (snapshot != null)
                return Drawable.createFromStream(snapshot.getInputStream(0), "img");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
    当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，
    否则就调用getCacheDir()方法来获取缓存路径。前者获取到的就是 /sdcard/Android/data/<application package>/cache
     这个路径，而后者获取到的是 /data/data/<application package>/cache 这个路径。
     */
    private File getDiskCacheDir(Context context) {
        String cachePath;

        //判断SD卡是否已经挂载，并且挂载点可读/写或SD卡是否可移除
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (context.getExternalCacheDir() != null)
                cachePath = context.getExternalCacheDir().getPath();
            else {
                cachePath = context.getCacheDir().getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private byte[] drawableToByte(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        return bao.toByteArray();
    }

    private Drawable byteToDrawable(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}


