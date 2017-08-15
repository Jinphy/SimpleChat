package com.example.jinphy.simplechat.modules.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.model.Message;

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
        fragment.setCallback(this::getPresenter);
        addFragment(fragment, R.id.fragment);

        getPresenter(fragment);

    }

    @Override
    public ChatPresenter getPresenter(@NonNull Fragment fragment) {
        return new ChatPresenter((ChatContract.View) fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
