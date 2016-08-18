package com.tr.zps.app.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tr.zps.app.coolweather.service.AutoUpdateService;

/**
 * Created by zps on 2016/8/18.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startService(intent1);
    }
}
