package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;


public class SignUpActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpFragment fragment = SignUpFragment.newInstance();

        SignUpFragment returnFragment = (SignUpFragment) addFragment(fragment, R.id.fragment);
        getPresenter(returnFragment);
    }

    @Override
    public SignUpPresenter getPresenter(Fragment fragment) {
        return new SignUpPresenter(this, (SignUpContract.View) fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
