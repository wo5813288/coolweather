package com.lin.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.coolweather.db.UserCity;
import com.lin.coolweather.fragment.ChooseAreaFragment;
import com.lin.coolweather.gson.Forecast;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.service.AutoUpdateService;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.PreferenceUtil;
import com.lin.coolweather.util.ToastUtil;
import com.lin.coolweather.util.Utility;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherActivity extends BaseActivity{
    /**
     * 添加城市
     */
    public static final int ADD_REQUEST_CODE = 1;
    /**
     * 用户保存的城市列表
     */
    public static final int CITY_LIST_REQUEST_CODE = 2;
    public static final String FOR_RESULT_KEY = "weather_id";
    private NestedScrollView svWeatherLayout;
    private TextView tvTitleCity,tvTitleUpdateTime,tvDegree,tvWeatherInfo;
    private LinearLayout forecastLayout;
    private TextView tvAqi,tvPm25,tvComfort,tvCarWash,tvSport;
    private TextView tvWeatherMinMax,titleMore;
    private ImageView ivWeatherBg,ivWeatherPng;
    private String mWeatherId;
    public DrawerLayout weatherDrawerLayout;
    public Button btNav;
    private ImageView ivWeatherRefresh;
    private boolean isAddCity = false;
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
    }
    /**
     * 初始化控件
     */
    private void initView() {
        Toolbar toolbar = findViewById(R.id.weather_tool_bar);
        setSupportActionBar(toolbar);
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
        weatherDrawerLayout = findViewById(R.id.weather_drawer_layout);
        btNav = findViewById(R.id.bt_nav);
        btNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        ivWeatherPng = findViewById(R.id.iv_weather_png);
        tvWeatherMinMax = findViewById(R.id.tv_weather_min_max);
        ivWeatherRefresh = findViewById(R.id.iv_weather_refresh);
        ivWeatherRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWeather(mWeatherId);
            }
        });
        titleMore = findViewById(R.id.title_more);
        titleMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PopupMenu popupMenu = new PopupMenu(WeatherActivity.this,v, Gravity.CENTER);
               popupMenu.getMenuInflater().inflate(R.menu.title_menu,popupMenu.getMenu());
               popupMenu.show();
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(android.view.MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.title_add_city:
                               Intent intent = new Intent(WeatherActivity.this,AddCityActivity.class);
                               startActivityForResult(intent,ADD_REQUEST_CODE);
                               break;
                           case R.id.title_city:
                               Intent citList =new Intent(WeatherActivity.this,CityListActivity.class);
                               startActivityForResult(citList,CITY_LIST_REQUEST_CODE);
                               break;
                           case R.id.title_setting:
                               break;
                               default:
                       }
                       return false;
                   }
               });
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
        ivWeatherRefresh.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bt_refresh_roate));
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
                            if(isAddCity){
                                //保存用户的城市信息
                                saveMyCity(weatherId,response);
                            }
                            showWeatherInfo(weather);
                        }else{
                            ToastUtil.toast(WeatherActivity.this,"获取天气数据失败");
                        }
                        ivWeatherRefresh.clearAnimation();
                    }
                });
            }

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toast(WeatherActivity.this,"获取天气数据失败");
                        //srLayout.setRefreshing(false);
                        ivWeatherRefresh.clearAnimation();
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
       // String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String updateTime = weather.basic.update.updateTime.replaceAll("-","/");
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        String weatherPngCode = weather.now.more.code;
        tvTitleCity.setText(cityName);
        tvTitleUpdateTime.setText(updateTime);
        tvDegree.setText(degree);
        tvWeatherInfo.setText(weatherInfo);
        tvWeatherMinMax.setText(weather.now.getWind());
        Glide.with(this).load("file:///android_asset/"+weatherPngCode+".png").into(ivWeatherPng);
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
        Intent intent =new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    isAddCity = true;
                    String weatherId = data.getStringExtra(FOR_RESULT_KEY);
                    requestWeather(weatherId);
                }
                break;
            case CITY_LIST_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    String weatherId = data.getStringExtra(FOR_RESULT_KEY);
                    requestWeather(weatherId);
                }
                default:
        }
    }

    /**
     * 保存用户自己的城市
     */
    private void saveMyCity(String cityCN,String content){
        UserCity userCity = new UserCity();
        userCity.setCityId(cityCN);
        userCity.setWeatherContent(content);
        userCity.save();
        isAddCity = false;
    }
}
