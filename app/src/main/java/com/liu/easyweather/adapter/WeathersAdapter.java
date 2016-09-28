package com.liu.easyweather.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.liu.easyweather.bean.Weather;
import com.liu.easyweather.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu on 2016-09-27.
 */
public class WeathersAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public WeathersAdapter(FragmentManager fm) {
        super(fm);
    }

    public WeathersAdapter(FragmentManager fm,List<String> cityNames) {
        super(fm);
        if (cityNames!=null&&cityNames.size()!=0){
            fragments=new ArrayList<>();
            for (String cityName:cityNames){
                fragments.add(MainFragment.newInatance(cityName));
            }
        }

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
