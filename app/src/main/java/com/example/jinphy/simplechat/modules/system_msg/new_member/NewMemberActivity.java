package com.example.jinphy.simplechat.modules.system_msg.new_member;

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
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsFragment;

public class NewMemberActivity extends BaseActivity {


    private ActionBar actionBar;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NewMemberActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(NewMemberFragment.newInstance(), R.id.fragment));
    }

    @Override
    public NewMemberPresenter getPresenter(Fragment fragment) {
        return new NewMemberPresenter((NewMemberContract.View) fragment);
    }

    public void updateTitle(int count) {
        if (count == 0) {
            actionBar.setTitle("新成员");
        } else {
            actionBar.setTitle("新成员(" + count + ")");
        }
    }
}
