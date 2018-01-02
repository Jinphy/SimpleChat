package com.example.jinphy.simplechat.modules.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.custom_view.LoadingDialog;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.lang.reflect.Field;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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
