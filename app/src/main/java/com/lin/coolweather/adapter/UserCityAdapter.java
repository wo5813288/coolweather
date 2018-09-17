package com.lin.coolweather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.coolweather.R;
import com.lin.coolweather.gson.Weather;

import java.util.List;

public class UserCityAdapter extends RecyclerView.Adapter<UserCityAdapter.UserCityHolder> {

    private List<Weather> weatherList;
    private Context context;
    public UserCityAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }
    private OnItemClickListener itemClickListener;
    private View view;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(Weather weather);
    }
    @NonNull
    @Override
    public UserCityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
         view = LayoutInflater.from(context).inflate(R.layout.user_city_item,parent,false);
        UserCityHolder holder = new UserCityHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserCityHolder holder, int position) {
        final Weather weather = weatherList.get(position);
        String code = weather.now.more.code;
        holder.cityName.setText(weather.basic.cityName);
        holder.time.setText(weather.basic.update.updateTime);
        holder.cityTmp.setText(weather.now.temperature+"℃");
        holder.cityMinMax.setText(weather.now.windDir+" "+weather.now
        .windSc);
        Glide.with(context).load("file:///android_asset/"+code+".png").into(holder.cityBg);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(weather);
            }
        });
    }


    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class UserCityHolder extends RecyclerView.ViewHolder{
        private TextView cityName,time,cityTmp,cityMinMax;
        private ImageView cityBg;
        public UserCityHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.user_city_item_name);
            time = itemView.findViewById(R.id.user_city_item_time);
            cityTmp = itemView.findViewById(R.id.user_city_item_tmp);
            cityMinMax = itemView.findViewById(R.id.user_city_item_min_max);
            cityBg  =itemView.findViewById(R.id.user_city_item_bg);
        }
    }
}
