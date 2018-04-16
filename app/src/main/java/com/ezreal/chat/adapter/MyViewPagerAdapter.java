package com.ezreal.chat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/16.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mData;
    private FragmentManager manager;

    public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> mList) {
        super(fm);
        this.manager = fm;
        this.mData = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
