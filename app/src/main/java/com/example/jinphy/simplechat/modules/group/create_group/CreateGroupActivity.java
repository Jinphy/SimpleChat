package com.example.jinphy.simplechat.modules.group.create_group;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class CreateGroupActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, CreateGroupActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("新建群聊");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(CreateGroupFragment.newInstance(), R.id.fragment));
    }

    @Override
    public CreateGroupPresenter getPresenter(Fragment fragment) {
        return new CreateGroupPresenter((CreateGroupContract.View) fragment);
    }
}
