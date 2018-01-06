package com.example.jinphy.simplechat.modules.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class LoginActivity extends BaseActivity {

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
