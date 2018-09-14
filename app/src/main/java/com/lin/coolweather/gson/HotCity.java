package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 热门城市列表
 */
public class HotCity {
    public String status;
    @SerializedName("basic")
    public List<HotCityBasic>  hotCityBasics;
}
