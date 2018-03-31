package com.example.jinphy.simplechat.modules.show_photo;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class ShowPhotoActivity extends BaseActivity {

    public static void start(Activity activity, long msgId, int position) {
        Intent intent = new Intent(activity, ShowPhotoActivity.class);
        intent.putExtra(ShowPhotoFragment.SAVE_KEY_MSG_ID, msgId);
        intent.putExtra(ShowPhotoFragment.SAVE_KEY_MSG_POSITION, position);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        Intent intent = getIntent();
        long msgId = intent.getLongExtra(ShowPhotoFragment.SAVE_KEY_MSG_ID, -1);
        int position = intent.getIntExtra(ShowPhotoFragment.SAVE_KEY_MSG_POSITION, -1);
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
