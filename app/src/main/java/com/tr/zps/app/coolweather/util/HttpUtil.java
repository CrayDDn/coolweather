package com.tr.zps.app.coolweather.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zps on 2016/8/16.
 */
public class HttpUtil {

    /**
     * 发送请求
     * @param address
     * @param listener
     */
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    if(listener!=null){
                        listener.onFinish(in);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
