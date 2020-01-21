package com.bluetouch.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bluetouch.fragment.BlueMainOneFragment;
import com.bluetouch.fragment.BlueMianTwoFragment;

import java.util.List;

/**
 * Created by User on 2016-03-02.
 */
public class BlueMainAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String strid;
    private int realPosition;


    public BlueMainAdapter(FragmentManager fm, List<Fragment> fragments, String strid, int position) {
        super(fm);
        this.fragments = fragments;
        this.strid = strid;
        this.realPosition = position;
    }

    @Override
    public Fragment getItem(int position) {

        if (this.fragments.get(position) instanceof BlueMainOneFragment) {
            return BlueMainOneFragment.newInstacne(position, strid, realPosition);
        } else if (this.fragments.get(position) instanceof BlueMianTwoFragment) {
            return BlueMianTwoFragment.newInstacne(position, strid, realPosition);
        } else {
            return BlueMainOneFragment.newInstacne(position, strid, realPosition);
        }

    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
