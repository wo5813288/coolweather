package com.lin.coolweather;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.coolweather.adapter.HotCityAdapter;
import com.lin.coolweather.gson.HotCity;
import com.lin.coolweather.gson.HotCityBasic;
import com.lin.coolweather.util.HttpUtil;
import com.lin.coolweather.util.PreferenceUtil;
import com.lin.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class AddCityActivity extends BaseActivity implements View.OnClickListener {
    public static final String HOT_URL = "https://search.heweather.com/top?group=cn&key=880167c3e1c04cf3ba44bdc6059c9497&number=20&lang=cn";
    private ImageView addCityBack;
    private TextView addCitySearch;
    private RecyclerView addCityRecyclerView;
    private HotCityAdapter adapter;
    private List<HotCityBasic> hotCityBasics = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        initView();
        initData();
        adapter = new HotCityAdapter(hotCityBasics);
        adapter.setListener(new HotCityAdapter.OnItemClickListener() {
            @Override
            public void onClick(HotCityBasic hotCityBasic) {
                Intent intent = new Intent();
                intent.putExtra("weather_id",hotCityBasic.cid);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        addCityRecyclerView.setAdapter(adapter);
    }

    /**
     * 初始化绑定数据
     */
    private void initData() {
        String sharedPreferenceString = PreferenceUtil.getSharedPreferenceString(this, PreferenceUtil.HOT_CITY_KEY, null);
        if(sharedPreferenceString!=null){
            HotCity hotCity = Utility.handleHotCityResponse(sharedPreferenceString);
            hotCityBasics.addAll(hotCity.hotCityBasics);
            return;
        }
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.ResponseListener() {
            @Override
            public void onSuccess(final String response) {
                final HotCity hotCity = Utility.handleHotCityResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(hotCity!=null&&hotCity.status.equals("ok")){
                            PreferenceUtil.setSharedPreferenceString(AddCityActivity.this,PreferenceUtil.HOT_CITY_KEY,response);
                            hotCityBasics.addAll(hotCity.hotCityBasics);
                        }
                    }
                });

            }

            @Override
            public void onFail() {

            }
        });
        httpUtil.sendOKhttpRequest(HOT_URL);
    }

    private void initView() {
        addCityBack = findViewById(R.id.add_city_back);
        addCityBack.setOnClickListener(this);
        addCitySearch = findViewById(R.id.add_city_search);
        addCitySearch.setOnClickListener(this);
        addCityRecyclerView = findViewById(R.id.add_city_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        addCityRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_city_back:
                finish();
                break;
            case R.id.add_city_search:
                break;
                default:
                    break;
        }
    }
}
