package com.lin.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonArray;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.LocationUtil;
import com.lin.coolweather.util.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
   private LocationUtil locationUtil;
    private MyLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断有没有天气缓存，如果有缓存则直接显示，如果没有缓存信息则网络获取
        String weatherContent = PreferenceUtil.getSharedPreferenceString(this, PreferenceUtil.WEATHER_KEY, null);
        if(weatherContent!=null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }else{
            //声明LocationClient类
            locationUtil = new LocationUtil(getApplicationContext());
            //注册监听函数
            locationUtil.startLocatin(myListener);
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取位置描述信息相关的结果
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            Log.e("locationMsg",district+"====="+city+"=="+longitude+"==="+latitude);
            HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
                @Override
                public void onSuccess(final String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                closeProgressDialog();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                                String status = jsonArray.getJSONObject(0).getString("status");
                                if(status.equals("ok")){
                                    JSONArray jsonArray1 = jsonArray.getJSONObject(0).getJSONArray("basic");
                                    String cid = jsonArray1.getJSONObject(0).getString("cid");
                                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                                    intent.putExtra("weather_id",cid);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFail() {

                }
            });
            httpUtil.sendOKhttpRequest("https://search.heweather.com/find?location="+city+"&key=880167c3e1c04cf3ba44bdc6059c9497&");
        }
    }
    private ProgressDialog progressDialog;
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在获取位置信息...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
