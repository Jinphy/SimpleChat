package com.example.jinphy.simplechat.modules.show_file;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.modules.show_photo.ShowPhotoFragment;

public class ShowFileActivity extends BaseActivity {


    public static void start(Activity activity, long msgId, int position) {
        Intent intent = new Intent(activity, ShowFileActivity.class);
        intent.putExtra(ShowFileFragment.SAVE_KEY_MSG_ID, msgId);
        intent.putExtra(ShowFileFragment.SAVE_KEY_MSG_POSITION, position);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_file);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("文件");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        Intent intent = getIntent();
        long msgId = intent.getLongExtra(ShowPhotoFragment.SAVE_KEY_MSG_ID, -1);
        int position = intent.getIntExtra(ShowPhotoFragment.SAVE_KEY_MSG_POSITION, -1);
        if (msgId == -1) {
            finish();
            return;
        }

        getPresenter(addFragment(ShowFileFragment.newInstance(msgId,position), R.id.fragment));
    }

    @Override
    public ShowFilePresenter getPresenter(Fragment fragment) {
        return new ShowFilePresenter((ShowFileContract.View) fragment);
    }
}
