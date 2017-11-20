package com.example.jinphy.simplechat.modules.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.jinphy.simplechat.constants.StringConst;
import com.example.jinphy.simplechat.model.event_bus.EBLoginInfo;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.utils.AnimUtils;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.net.URI;
import java.net.URISyntaxException;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private MainPresenter presenter;


    public static void start(Activity activity, User user, boolean rememberPassword) {
        Flowable.just("startActivity")
                .map(flag -> {
                    SharedPreferences.Editor edit =
                            activity.getSharedPreferences(StringConst.PREFERENCES_NAME_USER,
                                    Context.MODE_PRIVATE).edit();
                    edit.putString(StringConst.PREFERENCES_KEY_CURRENT_ACCOUNT, user.getAccount());
                    edit.putString(StringConst.PREFERENCES_KEY_PASSWORD, user.getPassword());
                    edit.putBoolean(StringConst.PREFERENCES_KEY_REMEMBER_PASSWORD,
                            rememberPassword);
                    edit.putBoolean(StringConst.PREFERENCES_KEY_HAS_LOGIN, true);
                    edit.apply();
                    return new EBLoginInfo(rememberPassword, user.getAccount());
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(loginInfo -> EventBus.getDefault().postSticky(loginInfo))
                .subscribe();
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

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
        MainFragment fragment = null;
        try {
            fragment = MainFragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainFragment returnFragment = (MainFragment) addFragment(fragment, R.id.fragment);
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
