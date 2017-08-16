package com.example.jinphy.simplechat.modules.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class ChatActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        ChatFragment fragment = ChatFragment.newInstance();

        ChatFragment returnFragment = (ChatFragment) addFragment(fragment, R.id.fragment);
        returnFragment.setPresenterCallback(this::getPresenter);

    }

    @Override
    public ChatPresenter getPresenter(@NonNull Fragment fragment) {
        return new ChatPresenter((ChatContract.View) fragment);
    }

}
