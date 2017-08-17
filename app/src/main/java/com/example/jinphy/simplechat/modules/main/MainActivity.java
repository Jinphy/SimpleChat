package com.example.jinphy.simplechat.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.utils.AnimUtils;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp);
        actionBar.setTitle(R.string.app_name);
        MainFragment fragment = MainFragment.newInstance();

        MainFragment returnFragment = (MainFragment) addFragment(fragment, R.id.fragment);
        returnFragment.setPresenterCallback(this::getPresenter);
        presenter = getPresenter(returnFragment);



    }


    @Override
    public MainPresenter getPresenter(Fragment fragment) {

        return new MainPresenter((MainContract.View) fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    protected boolean handleTouchEvent() {
        return ((MainFragment) baseFragment).currentItemPosition()==3;
    }
}
