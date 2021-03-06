package com.example.jinphy.simplechat.modules.active_zoom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

public class ActiveZoneActivity extends BaseActivity {

    private ActiveZonePresenter presenter;


    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ActiveZoneActivity.class);

        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_zone);

//        ScreenUtils.setStatusBarColor(this,0xff000000);

        presenter = getPresenter(addFragment(ActiveZoneFragment.newInstance(), R.id.fragment));

    }

    @Override
    public ActiveZonePresenter getPresenter(Fragment fragment) {
        return new ActiveZonePresenter((ActiveZoneFragment) fragment);
    }

}
