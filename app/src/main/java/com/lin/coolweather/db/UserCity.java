package com.lin.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 用户保存的城市类
 */
public class UserCity extends DataSupport {
    private int id;
    private String CityId;
    private String weatherContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getWeatherContent() {
        return weatherContent;
    }

    public void setWeatherContent(String weatherContent) {
        this.weatherContent = weatherContent;
    }
}
