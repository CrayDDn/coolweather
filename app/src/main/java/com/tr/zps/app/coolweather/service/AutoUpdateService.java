package com.tr.zps.app.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tr.zps.app.coolweather.receiver.AutoUpdateReceiver;
import com.tr.zps.app.coolweather.util.HttpCallBackListener;
import com.tr.zps.app.coolweather.util.HttpUtil;
import com.tr.zps.app.coolweather.util.Utility;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created by zps on 2016/8/18.
 */
public class AutoUpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long threeHour = 3 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + threeHour;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent1,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("county_name", "");
        try {
            if(!TextUtils.isEmpty(name)){
                String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + URLEncoder.encode(name, "UTF-8") +
                        "&key=36ba9548cef512571bc8e45864174923";
                HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                    @Override
                    public void onFinish(InputStream in) {
                        Utility.handleWeatherResponse(AutoUpdateService.this,in);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
