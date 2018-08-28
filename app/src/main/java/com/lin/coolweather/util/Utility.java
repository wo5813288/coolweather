package com.lin.coolweather.util;

import android.text.TextUtils;

import com.lin.coolweather.db.City;
import com.lin.coolweather.db.County;
import com.lin.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;


public class Utility {
    /**
     * 处理省的数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
               for(int i=0;i<allProvinces.length();i++){
                   JSONObject provinceObject = allProvinces.getJSONObject(i);
                   Province province = new Province();
                   province.setProvinceCode(provinceObject.getInt("id"));
                   province.setProvinceName(provinceObject.getString("name"));
                   province.save();
               }
               return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理市的数据
     * @param response
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject provinceObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(provinceObject.getInt("id"));
                    city.setCityName(provinceObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理县的数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties= new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject provinceObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setWeatherId(provinceObject.getString("weather_id"));
                    county.setCountyName(provinceObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
