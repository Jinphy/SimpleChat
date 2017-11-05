package com.example.jinphy.simplechat.modules.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

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
