package com.tr.zps.app.coolweather.util;

import android.util.JsonReader;

import com.tr.zps.app.coolweather.db.CoolWeatherDB;
import com.tr.zps.app.coolweather.model.City;
import com.tr.zps.app.coolweather.model.County;
import com.tr.zps.app.coolweather.model.Province;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zps on 2016/8/16.
 */
public class Utility {


    /**
     * 解析和处理服务器返回数据
     * @param coolWeatherDB
     * @param in
     * @return
     */
    public static boolean handleResponse(CoolWeatherDB coolWeatherDB, InputStream in){
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        boolean flag = false;
        try {
            reader.beginObject();
            while (reader.hasNext()){
                String nodeName = reader.nextName();
                if(nodeName.equals("resultcode")){
                    LogUtil.log("Utility", "resultcode = " + reader.nextString(), LogUtil.NOTHING);
                    flag = true;
                }else if (nodeName.equals("result") && flag){
                    saveAreaToDataBase(coolWeatherDB,reader);
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 把数据保存到数据库中
     * @param reader
     * @return
     */
    private static boolean saveAreaToDataBase(CoolWeatherDB coolWeatherDB,JsonReader reader){
        String provinceName = null;
        String cityName = null;
        String countyName = null;
        List<String> provinceNames = new ArrayList<>();
        List<String> cityNames = new ArrayList<>();
        boolean changedProvince = false;
        boolean changedCity = false;
        int provinceId = 0;
        int cityId = 0;
        int countyId = 0;
        Province previousProvince = new Province();
        City previousCity = new City();
        try {
            reader.beginArray();
            while (reader.hasNext()){
                reader.beginObject();
                while (reader.hasNext()){
                    String nodeName = reader.nextName();
                    if(nodeName.equals("province")){
                        provinceName = reader.nextString().trim();
                        if(!provinceNames.contains(provinceName)){
                            provinceNames.add(provinceName);
                            changedProvince = true;
                            provinceId++;
                        }
                    }else if(nodeName.equals("city")){
                        cityName = reader.nextString().trim();
                        if(!cityNames.contains(cityName)){
                            cityNames.add(cityName);
                            changedCity = true;
                            cityId++;
                        }
                    }else if (nodeName.equals("district")){
                        countyName = reader.nextString().trim();
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if(changedProvince){
                    Province province = new Province();
                    province.setId(provinceId);
                    province.setProvinceName(provinceName);
                    previousProvince = province;
                    coolWeatherDB.saveProvince(province);
                    changedProvince = false;
                }
                if(changedCity){
                    City city = new City();
                    city.setId(cityId);
                    city.setCityName(cityName);
                    city.setProvinceId(previousProvince.getId());
                    previousCity = city;
                    coolWeatherDB.saveCity(city);
                    changedCity = false;
                }
                County county = new County();
                countyId++;
                county.setId(countyId);
                county.setCountyName(countyName);
                county.setCityId(previousCity.getId());
                coolWeatherDB.saveCounty(county);

            }
            reader.endArray();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
