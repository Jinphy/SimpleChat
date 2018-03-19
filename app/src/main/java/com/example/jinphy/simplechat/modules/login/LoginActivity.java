package com.example.jinphy.simplechat.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment fragment = LoginFragment.newInstance();

        LoginFragment returnFragment = (LoginFragment) addFragment(fragment, R.id.fragment);
        getPresenter(returnFragment);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public LoginPresenter getPresenter(Fragment fragment) {

        return new LoginPresenter(this, (LoginContract.View) fragment);

    }


}
