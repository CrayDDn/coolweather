package com.tr.zps.app.coolweather.util;

import java.io.InputStream;

/**
 * Created by zps on 2016/8/16.
 */
public interface HttpCallBackListener {

    public void onFinish(InputStream in);

    public void onError(Exception e);
}
