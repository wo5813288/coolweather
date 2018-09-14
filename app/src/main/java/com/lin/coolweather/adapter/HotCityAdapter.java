package com.lin.coolweather.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lin.coolweather.R;
import com.lin.coolweather.gson.HotCity;
import com.lin.coolweather.gson.HotCityBasic;

import java.util.List;

public class HotCityAdapter extends RecyclerView.Adapter<HotCityAdapter.MyViewHolder> {


    private List<HotCityBasic> list;
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onClick(HotCityBasic hotCityBasic);
    }
    public HotCityAdapter(List<HotCityBasic> list) {
        this.list = list;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_city_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final HotCityBasic hotCityBasic = list.get(position);
        holder.bt.setText(hotCityBasic.location);
        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onClick(hotCityBasic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private Button bt;
        public MyViewHolder(View itemView) {
            super(itemView);
            bt = itemView.findViewById(R.id.hot_city_item_bt);
        }
    }
}

