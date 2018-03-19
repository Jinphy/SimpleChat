package com.example.jinphy.simplechat.modules.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.services.push.PushService;

public class ChatActivity extends BaseActivity {


    private ActionBar actionBar;

    public static void start(Activity activity, String withAccount) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatFragment.WITH_ACCOUNT, withAccount);
        activity.startActivity(intent);

        // 启动前先检测推送服务是否开启，没有开启时会再次启动推送服务，保证消息及时接收
        PushService.start(activity, PushService.FLAG_INIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(ChatFragment.newInstance(
                getIntent().getStringExtra(ChatFragment.WITH_ACCOUNT)), R.id.fragment));
    }

    @Override
    public ChatPresenter getPresenter(@NonNull Fragment fragment) {
        return new ChatPresenter((ChatContract.View) fragment);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        }
    }
}
