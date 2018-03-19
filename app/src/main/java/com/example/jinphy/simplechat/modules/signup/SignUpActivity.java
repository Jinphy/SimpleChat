package com.example.jinphy.simplechat.modules.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;


public class SignUpActivity extends BaseActivity {


    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getPresenter(addFragment(SignUpFragment.newInstance(),R.id.fragment));

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
