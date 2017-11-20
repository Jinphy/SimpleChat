package com.example.jinphy.simplechat.modules.welcome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.DBApplication;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.utils.PermissionUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_welcome);

        WelcomeFragment fragment = WelcomeFragment.newInstance();

        WelcomeFragment returnFragment = (WelcomeFragment) addFragment(fragment, R.id.fragment);
        getPresenter(returnFragment);


    }

    @Override
    public WelcomePresenter getPresenter(Fragment fragment) {

        return new WelcomePresenter((WelcomeContract.View) fragment);
    }

}
