package com.zbm.lovehealth.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Calendar;


public class MyUtil {
    public final static int TRANSMISSION_ERROR=0;
    public final static int CONNECTION_ERROR=1;


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * 判断软键盘是否弹出
     */
    public static boolean isSoftInputMethodShowing(Activity context) {

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;   //这个方法获取可能不是真实屏幕的高度(可能有虚拟导航栏)

        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return usableHeight - rect.bottom != 0;
    }

    /**
     * 计算底部导航栏高度
     */
    public static int getNavigationBarHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        return realHeight - usableHeight;
    }

    /**
     * 判断是否处于夜间
     */
    public static boolean isNight(Context context) {
        //获得内容提供者
        ContentResolver mResolver = context.getContentResolver();
        //获得系统时间制
        String timeFormat = Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
        //判断时间制
        Calendar mCalendar = Calendar.getInstance();
        if (timeFormat!=null&&timeFormat.equals("24")) {
            //24小时制
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            Log.d("MyUtil", "24小时制：" + hour);
            return hour >= 19;
        } else {
            //12小时制,mCalendar.get(Calendar.AM_PM))返回0为AM，反之返回1为PM
            Log.d("MyUtil", "12小时制：" + mCalendar.get(Calendar.AM_PM));
            return mCalendar.get(Calendar.AM_PM) != 0;
        }
    }

    /**
     * 验证网络是否连接
     * @param context .
     * @return .
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return networkinfo != null && networkinfo.isAvailable();
    }
}
