package com.zbm.lovehealth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zbm.lovehealth.utils.CacheUtil;
import com.zbm.lovehealth.utils.MyUtil;

public class LoveHealthApplication extends Application {
    private RefWatcher refWatcher;


    public static RefWatcher getRefWatcher(Context context) {
        LoveHealthApplication application = (LoveHealthApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CacheUtil.initCache(this);
        SharedPreferences.Editor editor=getSharedPreferences("Love Health",MODE_PRIVATE).edit();
        if (MyUtil.isNight(this))
            editor.putBoolean("is_night",true);
        else
            editor.putBoolean("is_night",false);
        editor.apply();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }
}
