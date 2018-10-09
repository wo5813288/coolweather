package com.lin.coolweather.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lin.coolweather.MainActivity;

/**
 * 定位工具类
 */
public class LocationUtil {

    private  LocationClient mLocationClient = null;

    private Context context;

    public LocationUtil(Context context) {
        this.context = context;
        mLocationClient = new LocationClient(context);
        initLocation();
    }

    /**
     * 开启定位
     * @param listener
     */
    public void startLocatin(BDAbstractLocationListener listener){
        mLocationClient.registerLocationListener(listener);
        mLocationClient.start();
    }

    /**
     * 配置百度sdk定位
     */
    private  void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(0);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);
        //是否需要地址信息
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    private static ProgressDialog progressDialog;
    public  void showProgressDialog(Context context){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在获取位置信息...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public  void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
