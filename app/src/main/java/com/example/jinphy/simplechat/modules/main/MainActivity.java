package com.example.jinphy.simplechat.modules.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.constants.StringConst;
import com.example.jinphy.simplechat.custom_libs.RuntimePermission;
import com.example.jinphy.simplechat.models.event_bus.EBLoginInfo;
import com.example.jinphy.simplechat.models.event_bus.EBNewMsg;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.services.push.PushService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private MainPresenter presenter;



    public static void start(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp);
        actionBar.setTitle(R.string.app_name);
        MainFragment fragment = null;
        try {
            fragment = MainFragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainFragment returnFragment = (MainFragment) addFragment(fragment, R.id.fragment);
        presenter = getPresenter(returnFragment);


        RuntimePermission.getInstance(this)
                .permission(Manifest.permission.READ_PHONE_STATE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .permission(Manifest.permission.CAMERA)
                .onReject(this::finish);
    }


    @Override
    public MainPresenter getPresenter(Fragment fragment) {

        return new MainPresenter(this, (MainContract.View) fragment);
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
