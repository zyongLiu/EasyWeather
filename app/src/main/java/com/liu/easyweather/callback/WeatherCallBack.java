package com.liu.easyweather.callback;

import com.google.gson.Gson;
import com.liu.easyweather.bean.Weather;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Liu on 2016-09-20.
 */
public abstract class WeatherCallBack extends Callback<Weather>{
    @Override
    public Weather parseNetworkResponse(Response response, int id) throws Exception {
        Gson gson=new Gson();
        return gson.fromJson(response.body().string().replace(" data service 3.0",""),Weather.class);
    }

}
