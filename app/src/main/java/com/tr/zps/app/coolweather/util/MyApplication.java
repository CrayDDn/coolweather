package com.tr.zps.app.coolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by zps on 2016/8/17.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
