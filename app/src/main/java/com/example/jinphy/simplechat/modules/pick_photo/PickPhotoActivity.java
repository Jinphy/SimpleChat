package com.example.jinphy.simplechat.modules.pick_photo;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.models.event_bus.EBIntent;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.annotations.NonNull;

public class PickPhotoActivity extends BaseActivity {

    public static final String OPTION = "OPTION";

    /**
     * DESC: 启动获取图片并裁剪图片的任务
     *
     *
     * @param option 选择，拍照或者从相册选取图片
     * Created by jinphy, on 2018/1/10, at 9:45
     */
    public static void start(Activity activity,@NonNull Option option,String tag) {
        if (option == null) {
            ObjectHelper.throwRuntime("option cannot be null!");
        }
        if (activity != null) {
            Intent intent = new Intent(activity, PickPhotoActivity.class);
            intent.putExtra(OPTION, option.get());
            intent.putExtra(PickPhotoFragment.TAG, tag);
            activity.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("图片裁剪");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        String option = getIntent().getStringExtra(OPTION);
        String tag = getIntent().getStringExtra(PickPhotoFragment.TAG);
        getPresenter(addFragment(PickPhotoFragment.newInstance(option, tag), R.id.fragment));

    }

    @Override
    public PickPhotoPresenter getPresenter(Fragment fragment) {

        return new PickPhotoPresenter(this, (PickPhotoContract.View) fragment);
    }

    /**
     * DESC: 获取图片成功后把数据发送被{@link PickPhotoFragment#receiveBitmapFromActivity(EBIntent)}
     * Created by jinphy, on 2018/1/10, at 11:26
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            LogUtils.e("null");
        }
        if (resultCode == RESULT_OK) {
            EventBus.getDefault().post(EBIntent.ok(requestCode,data));
        } else {
            EventBus.getDefault().post(EBIntent.error());
        }
    }

    /**
     * DESC: 获取图片的类型
     * Created by jinphy, on 2018/1/10, at 9:44
     */
    public enum Option {

        /**
         * DESC: 拍照
         * Created by jinphy, on 2018/1/10, at 9:43
         */
        TAKE_PHOTO("TAKE_PHOTO"),

        /**
         * DESC: 从相册选择
         * Created by jinphy, on 2018/1/10, at 9:43
         */
        CHOOSE_PHOTO("CHOOSE_PHOTO");


        private String value;

        Option(String value) {
            this.value = value;
        }
        public String get() {
            return value;
        }
    }
}
