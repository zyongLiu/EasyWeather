package com.liu.easyweather.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.liu.easyweather.R;
import com.liu.easyweather.adapter.WeathersAdapter;
import com.liu.easyweather.bean.ChinaCities;
import com.liu.easyweather.bean.Weather;
import com.liu.easyweather.callback.WeatherCallBack;
import com.liu.easyweather.utils.PreferencesUtils;
import com.liu.easyweather.utils.Utils;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;

public class MainActivity extends CheckPermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener {


//    private TextView tv_now_city;
//    private TextView tv_now_data;
//    private TextView tv_now_tem;
//
//    private ImageView iv_now_cloud;
//    private TextView tv_now_cloud;
//
//    private TextView tv_now_humidity;
//    private TextView tv_now_windspeed;
//    private TextView tv_now_visibility;
//    private TextView tv_now_uxindex;
//    private TextView tv_now_sunrise;
//    private TextView tv_now_sunset;
//
//    private LinearLayout ll_forecast;

    private ViewPager vip_content;

    private List<String> cityNames;

    private WeathersAdapter adapter;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "856224d7f18bb1ad2487af87e52ea157");
        setContentView(R.layout.activity_main);

        getCityNames();

        initView();

        //初始化定位
        initLocation();
        startLocation();

        //后台下载城市数据，保存到本地数据库
//        checkCityInfos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCityNames();
        if (cityNames!=null&&cityNames.size()!=0){


        adapter=new WeathersAdapter(getSupportFragmentManager(),cityNames);
        vip_content.setAdapter(adapter);}
    }

    /**
     * 从本地获取保存的城市名称列表
     */
    private void getCityNames() {
        String localCityNames=PreferencesUtils.getString(this,"CityNames","");
        if (!TextUtils.isEmpty(localCityNames)){
            cityNames=Arrays.asList(localCityNames.split(";"));
        }
    }

    private void checkCityInfos() {
        Log.i("TAG","开始插入");
        /**
         * 检查本地城市信息，如果没有则下载
         */
        OkHttpUtils.get().url("https://api.heweather.com/x3/citylist?search=allchina&key=c0f2347cbf8c43f5bf1a7ff2c52fd03c").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                Log.i("TAG","插入失败");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                ChinaCities chinaCities=gson.fromJson(response, ChinaCities.class);

                int count=chinaCities.getCity_info().size()/50+1;

                for (int i=0;i<count;i++){
                    List<BmobObject> persons = new ArrayList<BmobObject>();

                    if (i<count&&50*(i+1)<chinaCities.getCity_info().size())
                        persons.addAll(chinaCities.getCity_info().subList(50*i,50*(i+1)));
                    else
                        persons.addAll(chinaCities.getCity_info().subList(50*i,chinaCities.getCity_info().size()));

                    new BmobObject().insertBatch(MainActivity.this, persons, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("TAG","插入成功");
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("TAG","插入失败");
                        }
                    });
                }


            }
        });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vip_content= (ViewPager) findViewById(R.id.vip_content);

        if (cityNames!=null&&cityNames.size()!=0){
         adapter=new WeathersAdapter(getSupportFragmentManager(),cityNames);
        vip_content.setAdapter(adapter);}

//        ll_forecast= (LinearLayout) findViewById(R.id.ll_forecast);
//
//        tv_now_city= (TextView) findViewById(R.id.tv_now_city);
//        tv_now_data= (TextView) findViewById(R.id.tv_now_data);
//        tv_now_tem= (TextView) findViewById(R.id.tv_now_tem);
//
//        iv_now_cloud= (ImageView) findViewById(R.id.iv_now_cloud);
//        tv_now_cloud= (TextView) findViewById(R.id.tv_now_cloud);
//
//        tv_now_humidity= (TextView) findViewById(R.id.tv_now_humidity);
//        tv_now_windspeed= (TextView) findViewById(R.id.tv_now_windspeed);
//        tv_now_visibility= (TextView) findViewById(R.id.tv_now_visibility);
//        tv_now_uxindex= (TextView) findViewById(R.id.tv_now_uxindex);
//        tv_now_sunrise= (TextView) findViewById(R.id.tv_now_sunrise);
//        tv_now_sunset= (TextView) findViewById(R.id.tv_now_sunset);

    }

    public void setData(String cityname){
        if (cityNames==null||cityNames.size()==0){
            PreferencesUtils.putString(getApplicationContext(),"CityNames",cityname);
        }
        getCityNames();
        if (cityNames!=null&&cityNames.size()!=0){
        adapter=new WeathersAdapter(getSupportFragmentManager(),cityNames);
        vip_content.setAdapter(adapter);}
//        Weather.HeWeatherBean bean=weather.getHeWeather().get(0);
//
//        tv_now_city.setText(bean.getBasic().getCity()+","+weather.getHeWeather().get(0)
//        .getBasic().getCnty());
////        2016-09-20 16:52
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        try {
//            Date date = formatter.parse(bean.getBasic().getUpdate().getLoc());
//            SimpleDateFormat format2= new SimpleDateFormat("MM月dd日 HH时mm分");
//            tv_now_data.setText(format2.format(date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//            tv_now_data.setText("...");
//        }
//        tv_now_tem.setText(bean.getNow().getTmp()+"℃");
//
//        Picasso.with(this).load("http://files.heweather.com/cond_icon/"+bean.getNow()
//                .getCond().getCode()+".png").config(Bitmap.Config.RGB_565).into(iv_now_cloud);
//        tv_now_cloud.setText(bean.getNow().getCond().getTxt());
//
//        tv_now_humidity.setText("湿度："+bean.getNow().getHum()+"%");
//        tv_now_windspeed.setText("风速："+bean.getNow().getWind().getSpd()+"Kmph");
//
//        tv_now_visibility.setText("可见度："+bean.getNow().getVis()+"km");
//        tv_now_uxindex.setText("气压："+bean.getNow().getPres());
//        tv_now_sunrise.setText("风向："+bean.getNow().getWind().getDir());
//        tv_now_sunset.setText("降雨量："+bean.getNow().getPcpn()+"mm");
//
//        for (Weather.HeWeatherBean.DailyForecastBean b:bean.getDaily_forecast()){
//            View view=View.inflate(this,R.layout.item_daily_forecast,null);
//
//            SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date date = formatter3.parse(b.getDate());
//                SimpleDateFormat format4= new SimpleDateFormat("MM月dd日");
//                ((TextView)view.findViewById(R.id.tv_forecast_data)).setText(format4.format(date));
//            } catch (ParseException e) {
//                e.printStackTrace();
//                ((TextView)view.findViewById(R.id.tv_forecast_data)).setText("...");
//            }
//
//            ((TextView)view.findViewById(R.id.tv_forecast_tem)).setText(b.getTmp().getMin()+"℃~"+b.getTmp().getMax()+"℃");
//            ((TextView)view.findViewById(R.id.tv_forecast_cloud)).setText(b.getCond().getTxt_d());
//            Picasso.with(this).load("http://files.heweather.com/cond_icon/"+b
//                    .getCond().getCode_d()+".png").config(Bitmap.Config.RGB_565)
//                    .into((ImageView)view.findViewById(R.id.iv_forecast_cloud));
//            ll_forecast.addView(view,
//                    new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }
    }
    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(locationOption=getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(30*1000*60);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setLocationCacheEnable(true);
        return mOption;
    }


    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String result = Utils.getLocationStr(loc);

                String cityName=loc.getCity().replace("市","");
                setData(cityName);

//                OkHttpUtils.get().url("https://api.heweather.com/x3/weather?city="+cityName+"&key=c0f2347cbf8c43f5bf1a7ff2c52fd03c").build()
//                        .execute(new WeatherCallBack() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                    }
//
//                    @Override
//                    public void onResponse(Weather response, int id) {
//                        setData(response);
////                        if (cityNames)
//
//                    }
//                });

            } else {
            }
        }
    };


    @Override
    protected void onDestroy() {
        destroyLocation();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_city) {
            Intent intent=new Intent(this,CityListActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_cities) {

        }
        else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
