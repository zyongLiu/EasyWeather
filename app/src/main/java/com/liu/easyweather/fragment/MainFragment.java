package com.liu.easyweather.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.easyweather.R;
import com.liu.easyweather.bean.Weather;
import com.liu.easyweather.callback.WeatherCallBack;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;

/**
 * Created by Liu on 2016-09-22.
 */
public class MainFragment extends Fragment{

    private View view;

    private String cityName;

    private ImageView imv_now_forecast;
    private TextView tv_now_city;
    private TextView tv_now_data;
    private TextView tv_now_temp;
    private LinearLayout ll_forecast_content;

    public static MainFragment newInatance(String cityName) {
        MainFragment fragment=new MainFragment();
        fragment.cityName=cityName;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_main_new,null);
        initUI();
        initData();
        return view;
    }

    private void initUI() {
        imv_now_forecast= (ImageView) view.findViewById(R.id.imv_now_forecast);
        tv_now_city= (TextView) view.findViewById(R.id.tv_now_city);
        tv_now_data= (TextView) view.findViewById(R.id.tv_now_data);
        tv_now_temp= (TextView) view.findViewById(R.id.tv_now_temp);
        ll_forecast_content= (LinearLayout) view.findViewById(R.id.ll_forecast_content);
    }

    private void initData() {
        OkHttpUtils.get().url("https://api.heweather.com/x3/weather?city="+cityName+"&key=c0f2347cbf8c43f5bf1a7ff2c52fd03c").build()
                .execute(new WeatherCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(Weather response, int id) {
                        setData(response);
                    }
                });
    }

    public void setData(Weather weather) {
        Weather.HeWeatherBean bean=weather.getHeWeather().get(0);

        tv_now_city.setText(bean.getBasic().getCity()+","+weather.getHeWeather().get(0)
                .getBasic().getCnty());
//        2016-09-20 16:52
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = formatter.parse(bean.getBasic().getUpdate().getLoc());
            SimpleDateFormat format2= new SimpleDateFormat("MM月dd日 HH时mm分");
            tv_now_data.setText(format2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            tv_now_data.setText("...");
        }
        tv_now_temp.setText(bean.getNow().getTmp()+"°");

        Picasso.with(getActivity()).load("http://files.heweather.com/cond_icon/"+bean.getNow()
                .getCond().getCode()+".png").config(Bitmap.Config.RGB_565).into(imv_now_forecast);
    }
}
