package com.tr.zps.app.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tr.zps.app.coolweather.R;
import com.tr.zps.app.coolweather.util.HttpCallBackListener;
import com.tr.zps.app.coolweather.util.HttpUtil;
import com.tr.zps.app.coolweather.util.Utility;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zps on 2016/8/18.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于描述天气信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温
     */
    private TextView temperature;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 刷新天气按钮
     */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化控件
        cityNameText = (TextView) findViewById(R.id.city_name);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temperature = (TextView) findViewById(R.id.temperature);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        //按钮添加点击事件
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        //获取县级名称
        String county_name = getIntent().getStringExtra("county_name");
        //判断空
        if(!TextUtils.isEmpty(county_name)){
            weatherDespText.setText("同步天气中...");
            queryWeather(county_name);
        }else {

        }
    }

    /**
     * 查询天气
     * @param countyName
     */
    private void queryWeather(String countyName){
        try {
            String address = "http://v.juhe.cn/weather/index?format=2&cityname=" + URLEncoder.encode(countyName, "UTF-8") +
                    "&key=36ba9548cef512571bc8e45864174923";
            HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                @Override
                public void onFinish(InputStream in) {
                    Utility.handleWeatherResponse(WeatherActivity.this,in);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherDespText.setText("很遗憾，同步失败");
                        }
                    });
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从SharedPreferences文件中读取存储的天气数据
     */
    private void showWeather(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(pref.getString("city_name",""));
        weatherDespText.setText(pref.getString("weather",""));
        temperature.setText(pref.getString("temperature",""));
        currentDateText.setText(pref.getString("date",""));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                weatherDespText.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String name = preferences.getString("city_name", "");
                queryWeather(name);
                break;
            default:
                break;
        }
    }
}
