package com.example.jinphy.simplechat.modules.add_friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

public class AddFriendActivity extends BaseActivity {

    public static final String USER_JSON = "USER_JSON";

    public static void start(Activity activity, User user) {
        Intent intent = new Intent(activity, AddFriendActivity.class);
        intent.putExtra(USER_JSON, GsonUtils.toJson(user));
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("添加好友");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);


        String userJson = getIntent().getStringExtra(USER_JSON);
        if (userJson == null) {
            ObjectHelper.throwRuntime("userJson cannot be null!");
        }

        getPresenter(addFragment(AddFriendFragment.newInstance(userJson), R.id.fragment));
    }

    @Override
    public AddFriendPresenter getPresenter(Fragment fragment) {
        return new AddFriendPresenter(this, (AddFriendContract.View) fragment);
    }
}
