package com.example.jinphy.simplechat.modules.modify_user_info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class ModifyUserActivity extends BaseActivity {

    /**
     * DESC: 启动修改用户信息Activity
     * Created by jinphy, on 2018/1/8, at 11:09
     */
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ModifyUserActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.alpha_in,R.anim.anim_no);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("个人信息");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(ModifyUserFragment.newInstance(), R.id.fragment));
    }

    @Override
    public ModifyUserPresenter getPresenter(Fragment fragment) {
        return new ModifyUserPresenter(this, (ModifyUserContract.View) fragment);
    }


}
