package com.lin.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.PreferenceUtil;
import com.lin.coolweather.util.Utility;

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;//8小时
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新每日必应一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
            @Override
            public void onSuccess(String response) {
                PreferenceUtil.setSharedPreferenceString(AutoUpdateService.this,"bing_pic",response);
            }

            @Override
            public void onFail() {

            }
        });
        httpUtil.sendOKhttpRequest(requestBingPic);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        String sharedPreferenceString = PreferenceUtil.getSharedPreferenceString(this, PreferenceUtil.WEATHER_KEY, null);
        if(sharedPreferenceString!=null){
            //有缓存是直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(sharedPreferenceString);
            String weatherId = weather.basic.weatherId;
            String url = "http://guolin.tech/api/weather?cityid="+
                    weatherId+"&key=880167c3e1c04cf3ba44bdc6059c9497";
            HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Weather weather1 = Utility.handleWeatherResponse(response);
                    if(weather1!=null&&weather1.status.equals("ok")){
                        PreferenceUtil.setSharedPreferenceString(AutoUpdateService.this,PreferenceUtil.WEATHER_KEY,response);
                    }
                }

                @Override
                public void onFail() {

                }
            });
            httpUtil.sendOKhttpRequest(url);
        }
    }
}
