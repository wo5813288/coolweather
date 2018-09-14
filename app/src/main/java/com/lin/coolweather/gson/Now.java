package com.lin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    //风级
    @SerializedName("wind_sc")
    public String windSc;
    //风
    @SerializedName("wind_dir")
    public String windDir;
    public class More{
        @SerializedName("txt")
        public String info;
        public String code;
    }
    public String getWind(){
        int sc = Integer.valueOf(windSc);
        if(sc<3){
            return windDir+" "+"微风";
        }
        return windDir+" "+windSc+"级";
    }
}
