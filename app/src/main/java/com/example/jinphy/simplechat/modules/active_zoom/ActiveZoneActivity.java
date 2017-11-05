package com.example.jinphy.simplechat.modules.active_zoom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

public class ActiveZoneActivity extends BaseActivity {

    private ActiveZonePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_zone);

//        ScreenUtils.setStatusBarColor(this,0xff000000);

        ActiveZoneFragment fragment = ActiveZoneFragment.newInstance();

        ActiveZoneFragment returnFragment = (ActiveZoneFragment) addFragment(fragment, R.id.fragment);

        presenter = getPresenter(returnFragment);

    }

    @Override
    public ActiveZonePresenter getPresenter(Fragment fragment) {
        return new ActiveZonePresenter((ActiveZoneFragment) fragment);
    }


    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}
