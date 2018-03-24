package com.example.jinphy.simplechat.modules.show_photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserFragment;

public class ShowPhotoActivity extends BaseActivity {

    public static void start(Activity activity, long msgId, int position) {
        Intent intent = new Intent(activity, ShowPhotoActivity.class);
        intent.putExtra(ShowPhotoFragment.TAG_MSG_ID, msgId);
        intent.putExtra(ShowPhotoFragment.TAG_MSG_POSITION, position);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("图片");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        Intent intent = getIntent();
        long msgId = intent.getLongExtra(ShowPhotoFragment.TAG_MSG_ID, -1);
        int position = intent.getIntExtra(ShowPhotoFragment.TAG_MSG_POSITION, -1);
        if (msgId == -1) {
            finish();
            return;
        }

        getPresenter(addFragment(ShowPhotoFragment.newInstance(msgId,position), R.id.fragment));
    }

    @Override
    public ShowPhotoPresenter getPresenter(Fragment fragment) {
        return new ShowPhotoPresenter((ShowPhotoContract.View) fragment);
    }

}
