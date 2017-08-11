package com.example.jinphy.simplechat.modules.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainViewPagerAdapter(FragmentManager fm,@NonNull List<Fragment> fragments) {
        super(fm);
        this.fragments = Preconditions.checkNotNull(fragments);
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
