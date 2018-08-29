package com.lin.coolweather;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.coolweather.fragment.ChooseAreaFragment;
import com.lin.coolweather.gson.Forecast;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.PreferenceUtil;
import com.lin.coolweather.util.ToastUtil;
import com.lin.coolweather.util.Utility;

public class WeatherActivity extends BaseActivity{

    private ScrollView svWeatherLayout;
    private TextView tvTitleCity,tvTitleUpdateTime,tvDegree,tvWeatherInfo;
    private LinearLayout forecastLayout;
    private TextView tvAqi,tvPm25,tvComfort,tvCarWash,tvSport;
    private ImageView ivWeatherBg;
    public SwipeRefreshLayout srLayout;
    private String mWeatherId;
    public DrawerLayout weatherDrawerLayout;
    public Button btNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        String weatherContent = PreferenceUtil.getSharedPreferenceString(this, "weather", null);
        if(weatherContent!=null){
            //如果不为null，说明当前有缓存,直接显示缓存的内容
            Weather weather = Utility.handleWeatherResponse(weatherContent);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时从网络查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            svWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        String bingPic = PreferenceUtil.getSharedPreferenceString(this, "bing_pic", null);
        //说明当前有缓存的图片
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(ivWeatherBg);
        }else{
            loadBingPic();
        }
        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

    }
    /**
     * 初始化控件
     */
    private void initView() {
        svWeatherLayout = findViewById(R.id.sv_weather_layout);
        tvTitleCity =findViewById(R.id.title_city);
        tvTitleUpdateTime = findViewById(R.id.title_update_time);
        tvDegree = findViewById(R.id.degree_text);
        tvWeatherInfo = findViewById(R.id.tv_weather_info);
        forecastLayout = findViewById(R.id.ll_forecast_layout);
        tvAqi = findViewById(R.id.tv_aqi);
        tvPm25 = findViewById(R.id.tv_pm25);
        tvComfort = findViewById(R.id.tv_comfort);
        tvCarWash = findViewById(R.id.tv_car_wash);
        tvSport = findViewById(R.id.tv_sport);
        ivWeatherBg = findViewById(R.id.iv_weather_bg);
        srLayout = findViewById(R.id.sr_weather_layout);
        srLayout.setColorSchemeResources(R.color.colorPrimary);
        weatherDrawerLayout = findViewById(R.id.weather_drawer_layout);
        btNav = findViewById(R.id.bt_nav);
        btNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    /**
     * 获取天气页面的背景图片
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
            @Override
            public void onSuccess(final String response) {
                //将图片地址缓存
                PreferenceUtil.setSharedPreferenceString(WeatherActivity.this,"bing_pic",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(response).into(ivWeatherBg);
                    }
                });
            }

            @Override
            public void onFail() {

            }
        });
        httpUtil.sendOKhttpRequest(requestBingPic);
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+
                weatherId+"&key=880167c3e1c04cf3ba44bdc6059c9497";
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
            @Override
            public void onSuccess(final String response) {
                final Weather weather = Utility.handleWeatherResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&weather.status.equalsIgnoreCase("ok")){
                            PreferenceUtil.setSharedPreferenceString(WeatherActivity.this,"weather",response);
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else{
                            ToastUtil.toast(WeatherActivity.this,"获取天气数据失败");
                        }
                        srLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toast(WeatherActivity.this,"获取天气数据失败");
                        srLayout.setRefreshing(false);
                    }
                });
            }
        });
        httpUtil.sendOKhttpRequest(weatherUrl);
        loadBingPic();
    }

    /**
     * 处理并显示Weather天气数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        tvTitleCity.setText(cityName);
        tvTitleUpdateTime.setText(updateTime);
        tvDegree.setText(degree);
        tvWeatherInfo.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView tvDate = view.findViewById(R.id.tv_date);
            TextView tvInfo = view.findViewById(R.id.tv_info);
            TextView tvTmpMax = view.findViewById(R.id.tv_tmp_max);
            TextView tvTmpMin = view.findViewById(R.id.tv_tmp_min);
            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.more.info);
            tvTmpMax.setText(forecast.temperature.max);
            tvTmpMin.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            tvAqi.setText(weather.aqi.city.aqi);
            tvPm25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        tvComfort.setText(comfort);
        tvCarWash.setText(carWash);
        tvSport.setText(sport);
        svWeatherLayout.setVisibility(View.VISIBLE);
    }

}
