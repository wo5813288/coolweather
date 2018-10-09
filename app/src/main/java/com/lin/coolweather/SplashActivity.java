package com.lin.coolweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.coolweather.db.UserCity;
import com.lin.coolweather.util.ToastUtil;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {
    /**
     * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
     * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
     * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值（单位ms）
     **/
    private int minSplashTimeWhenNoAD = 2000;
    /**
     * 记录拉取广告的时间
     */
    private long fetchSplashADTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RelativeLayout container;
    private ImageView ivSpalshImage;
    private TextView skipView;
    /**
     * 判断是否可以跳过广告，进入MainActivity
     */
    private  boolean canJump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        container = findViewById(R.id.container);
        ivSpalshImage = findViewById(R.id.iv_splash_image);
        Glide.with(this).load(R.mipmap.weather_splash).into(ivSpalshImage);
        skipView = findViewById(R.id.skip_view);
        if(Build.VERSION.SDK_INT>=23){
            checkAndRequestPermission();
        }else{
            requestAds();
        }
    }

    /**
     * 检测运行时权限
     */
    private void checkAndRequestPermission(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        }else{
            requestAds();
        }
    }
    /**
     * 请求广告
     */
    private void requestAds() {
        fetchSplashADTime = System.currentTimeMillis();
        String appId = "1107843600";
        String adsId = "4070344060631394";
        new SplashAD(this, container, appId, adsId, new SplashADListener() {
            @Override
            public void onADDismissed() {
                //广告显示完毕
                forward();
            }

            @Override
            public void onNoAD(AdError adError) {
                //广告加载失败
                /**
                 * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
                 * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
                 * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值
                 **/
                long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;//从拉广告开始到onNoAD已经消耗了多少时间
                long shouldDelayMills = alreadyDelayMills > minSplashTimeWhenNoAD ? 0 : minSplashTimeWhenNoAD
                        - alreadyDelayMills;//为防止加载广告失败后立刻跳离开屏可能造成的视觉上类似于"闪退"的情况，根据设置的minSplashTimeWhenNoAD
                // 计算出还需要延时多久
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        SplashActivity.this.finish();
                    }
                }, shouldDelayMills);
            }

            @Override
            public void onADPresent() {
                //广告加载成功
                ivSpalshImage.setVisibility(View.GONE);
                skipView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onADClicked() {
                //广告被点击
            }

            @Override
            public void onADTick(long l) {

            }

            @Override
            public void onADExposure() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(canJump){
            forward();
        }
        canJump =false;
    }

    private void forward() {
        if(canJump){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            canJump = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            ToastUtil.toast(SplashActivity.this,"必须同意所有权限才能实用");
                            finish();
                            return;
                        }
                    }
                    requestAds();
                }else{
                    ToastUtil.toast(this,"发生未知错误");
                    finish();
                }
                break;
                default:
                    break;
        }
    }
}
