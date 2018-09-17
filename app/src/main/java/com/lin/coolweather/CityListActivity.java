package com.lin.coolweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lin.coolweather.BaseActivity;
import com.lin.coolweather.R;
import com.lin.coolweather.adapter.UserCityAdapter;
import com.lin.coolweather.db.UserCity;
import com.lin.coolweather.gson.Weather;
import com.lin.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends BaseActivity {

    private ImageView cityListBack;
    private RecyclerView cityListRecyclerView;
    private UserCityAdapter adapter;
    private List<Weather> weatherList = new ArrayList<>();
    private RelativeLayout btBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        initView();
        initData();
        adapter = new UserCityAdapter(weatherList);
        adapter.setItemClickListener(new UserCityAdapter.OnItemClickListener() {
            @Override
            public void onClick(Weather weather) {
                Intent intent =new Intent();
                intent.putExtra(WeatherActivity.FOR_RESULT_KEY,weather.basic.weatherId);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        cityListRecyclerView.setAdapter(adapter);
    }
    private  void initView(){
      btBack = findViewById(R.id.city_list_back);
      btBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });
        cityListRecyclerView = findViewById(R.id.city_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cityListRecyclerView.setLayoutManager(linearLayoutManager);
        cityListRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<UserCity> all = DataSupport.findAll(UserCity.class);
        if(all.size()>0) {
            for (int i = 0; i < all.size(); i++) {
                String weatherContent = all.get(i).getWeatherContent();
                Weather weather = Utility.handleWeatherResponse(weatherContent);
                weatherList.add(weather);
            }
        }
    }
}
