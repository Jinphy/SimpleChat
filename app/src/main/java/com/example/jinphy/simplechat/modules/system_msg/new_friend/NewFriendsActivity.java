package com.example.jinphy.simplechat.modules.system_msg.new_friend;

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
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgFragment;

public class NewFriendsActivity extends BaseActivity {

    private ActionBar actionBar;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NewFriendsActivity.class);
        activity.startActivity(intent);
        //        activity.overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("新朋友");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(NewFriendsFragment.newInstance(), R.id.fragment));
    }

    @Override
    public NewFriendsPresenter getPresenter(Fragment fragment) {
        return new NewFriendsPresenter((NewFriendsContract.View) fragment);
    }
    public void updateTitle(int count) {
        if (count == 0) {
            actionBar.setTitle("新朋友");
        } else {
            actionBar.setTitle("新朋友(" + count + ")");
        }
    }

}
