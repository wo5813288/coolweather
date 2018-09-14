package com.lin.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.coolweather.MainActivity;
import com.lin.coolweather.R;
import com.lin.coolweather.WeatherActivity;
import com.lin.coolweather.db.City;
import com.lin.coolweather.db.County;
import com.lin.coolweather.db.Province;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.ToastUtil;
import com.lin.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final String BASE_URL = "http://guolin.tech/api/china";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY= 2;
    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private Button btBack;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        tvTitle = view.findViewById(R.id.tv_title);
        btBack = view.findViewById(R.id.bt_back);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    //选中最后一级县，跳转到天气详情页面，关闭当前页面
                    String weatherId = countyList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                            WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.weatherDrawerLayout.closeDrawers();
                        activity. requestWeather(weatherId);
                    }

                }
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        //初始化时加载省份数据
        queryProvinces();
    }

    /**
     * 查询省份数据
     */
    private void queryProvinces(){
        tvTitle.setText("中国");
        btBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String url = "http://guolin.tech/api/china";
            queryFromServer(url,LEVEL_PROVINCE);
        }
    }
    private void queryCities(){
        tvTitle.setText(selectedProvince.getProvinceName());
        btBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int code = selectedProvince.getProvinceCode();
            String url = BASE_URL+"/"+code;
            queryFromServer(url,LEVEL_CITY);
        }
    }
    private void queryCounties(){
        tvTitle.setText(selectedCity.getCityName());
        btBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId()))
                .find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url = BASE_URL+"/"+provinceCode+"/"+cityCode;
            queryFromServer(url,LEVEL_COUNTY);
        }
    }

    /**
     * 从服务器查询数据
     * @param url
     * @param level
     */
    private void queryFromServer(String url, final int level){
        showProgressDialog();
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
            @Override
            public void onSuccess(String response) {
                boolean result = false;
                switch (level){
                    case LEVEL_PROVINCE:
                        result = Utility.handleProvinceResponse(response);
                        break;
                    case LEVEL_CITY:
                        result = Utility.handleCityResponse(response,selectedProvince.getId());
                        break;
                    case LEVEL_COUNTY:
                        result = Utility.handleCountyResponse(response,selectedCity.getId());
                        break;
                        default:
                            break;
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(level==LEVEL_PROVINCE){
                                queryProvinces();
                            }else if(level==LEVEL_CITY){
                                queryCities();
                            }else if(level ==LEVEL_COUNTY){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFail() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toast(getActivity(),"加载失败");
                    }
                });
            }
        });
        httpUtil.sendOKhttpRequest(url);
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
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
