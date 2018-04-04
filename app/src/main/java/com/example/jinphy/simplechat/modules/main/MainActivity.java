package com.example.jinphy.simplechat.modules.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.services.push.PushService;

public class MainActivity extends BaseActivity {

    private ActionBar actionBar;
    private MainPresenter presenter;


    private boolean hasCheckAccount;

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainFragment.FROM_LOGIN, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
//        context.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
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
        UserRepository.getInstance()
                .checkAccount(this, ()->{
                    PushService.start(this, PushService.FLAG_INIT);
                    hasCheckAccount = true;
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCheckAccount) {
            PushService.start(this, PushService.FLAG_INIT);
        }
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
        return ((MainFragment) baseFragment).getCheckedTab()==3;
    }

}
