package com.example.jinphy.simplechat.modules.system_msg;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.modules.chat.ChatFragment;
import com.example.jinphy.simplechat.modules.login.LoginActivity;

public class SystemMsgActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SystemMsgActivity.class);
        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_msg);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("系统消息");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(SystemMsgFragment.newInstance(), R.id.fragment));

    }

    @Override
    public SystemMsgPresenter getPresenter(Fragment fragment) {
        return new SystemMsgPresenter((SystemMsgContract.View) fragment);
    }
}
