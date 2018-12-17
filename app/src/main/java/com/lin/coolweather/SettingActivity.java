package com.lin.coolweather;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SettingActivity extends BaseActivity {

    private ImageView settingTitleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        settingTitleBack =findViewById(R.id.setting_title_back);
        settingTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
