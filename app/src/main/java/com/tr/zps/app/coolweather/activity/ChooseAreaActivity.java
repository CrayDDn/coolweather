package com.tr.zps.app.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tr.zps.app.coolweather.R;
import com.tr.zps.app.coolweather.db.CoolWeatherDB;
import com.tr.zps.app.coolweather.model.City;
import com.tr.zps.app.coolweather.model.County;
import com.tr.zps.app.coolweather.model.Province;
import com.tr.zps.app.coolweather.util.HttpCallBackListener;
import com.tr.zps.app.coolweather.util.HttpUtil;
import com.tr.zps.app.coolweather.util.MyApplication;
import com.tr.zps.app.coolweather.util.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zps on 2016/8/16.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog  progressDialog;

    private TextView titleText;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private CoolWeatherDB coolWeatherDB;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }

            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，数据库没有从服务器查询
     */
    private void queryProvinces(){
        provinceList = coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer("Province");
        }
    }

    /**
     * 查询市
     */
    private void queryCities(){
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer("City");
        }
    }

    private void queryCounties(){
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer("County");
        }
    }


    /**
     * 根据传入的类型从服务器查询省市县数据
     * @param type
     */
    private void queryFromServer(final String type){
        showProgressDialog();
        HttpUtil.sendHttpRequest("http://v.juhe.cn/weather/citys?key=36ba9548cef512571bc8e45864174923", new HttpCallBackListener() {
            @Override
            public void onFinish(InputStream in) {
                boolean result = Utility.handleResponse(CoolWeatherDB.getInstance(MyApplication.getContext()), in);
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("Province")){
                                queryProvinces();
                            }else if (type.equals("City")){
                                queryCities();
                            }else if(type.equals("County")){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){

        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载....");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {

        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
