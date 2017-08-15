package com.example.jinphy.simplechat.modules.active_zoom;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

public class ActiveZoneActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_zone);

        ScreenUtils.setStatusBarColor(this,0xff000000);

        ActiveZoneFragment fragment = ActiveZoneFragment.newInstance();
        addFragment(fragment,R.id.fragment);
    }

    @Override
    public ActiveZonePresenter getPresenter(Fragment fragment) {
        return new ActiveZonePresenter((ActiveZoneFragment) fragment);
    }
}
