package com.example.jinphy.simplechat.modules.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private List<Fragment> fragments;

    public MainViewPagerAdapter(FragmentManager fm,@NonNull List<Fragment> fragments) {
        super(fm);
        this.fragmentManager = fm;
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

    public static String getItemTag(ViewGroup container,int positin) {
        int viewId = container.getId();
        return "android:switcher:" + viewId + ":" + positin;
    }
}
