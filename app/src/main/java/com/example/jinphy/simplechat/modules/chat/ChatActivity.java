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

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.services.push.PushService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;

public class ChatActivity extends BaseActivity {


    private ActionBar actionBar;
    private ChatFragment fragment;

    public static void start(Activity activity, String withAccount) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatFragment.SAVE_KEY_WITH_ACCOUNT, withAccount);
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

        fragment = (ChatFragment) addFragment(ChatFragment.newInstance(
                getIntent().getStringExtra(ChatFragment.SAVE_KEY_WITH_ACCOUNT)), R.id.fragment);
        getPresenter(fragment);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            Observable.timer(500, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> fragment.onPickPhoto(photos))
                    .subscribe();
        }
    }
}
