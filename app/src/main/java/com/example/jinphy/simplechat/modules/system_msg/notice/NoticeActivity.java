package com.example.jinphy.simplechat.modules.system_msg.notice;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsContract;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsFragment;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsPresenter;

public class NoticeActivity extends BaseActivity {

    private ActionBar actionBar;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NoticeActivity.class);
        activity.startActivity(intent);
        //        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("公告");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(NoticeFragment.newInstance(), R.id.fragment));
    }

    @Override
    public NoticePresenter getPresenter(Fragment fragment) {
        return new NoticePresenter((NoticeContract.View) fragment);
    }

    public void updateTitle(int msgCount) {
        if (msgCount == 0) {
            actionBar.setTitle("公告");
        } else {
            actionBar.setTitle("公告(" + msgCount + ")");
        }
    }
}
