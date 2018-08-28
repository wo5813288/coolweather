package com.lin.coolweather.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private ResponseListener listener;
    public interface ResponseListener{
        void onSuccess(String response);
        void onFail();
    }

    public HttpUtil(ResponseListener listener) {
        this.listener = listener;
    }

    public void sendOKhttpRequest(String url){
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response.body().string());
            }
        });
    }

}
