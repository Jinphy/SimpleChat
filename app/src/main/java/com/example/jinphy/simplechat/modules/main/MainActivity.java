package com.example.jinphy.simplechat.modules.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.models.event_bus.EBInitDataAfterLogin;
import com.example.jinphy.simplechat.services.push.PushService;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private MainPresenter presenter;





    public static void start(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(MainFragment.FROM_LOGIN, false);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }


    public static void startFromLogin(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(MainFragment.FROM_LOGIN, true);
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
            fragment = MainFragment.newInstance(getIntent().getBooleanExtra(MainFragment.FROM_LOGIN,false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainFragment returnFragment = (MainFragment) addFragment(fragment, R.id.fragment);
        presenter = getPresenter(returnFragment);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PushService.start(this, PushService.FLAG_INIT);
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
