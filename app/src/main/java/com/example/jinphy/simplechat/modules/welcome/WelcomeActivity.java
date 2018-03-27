package com.example.jinphy.simplechat.modules.welcome;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.custom_libs.RuntimePermission;
import com.example.jinphy.simplechat.models.event_bus.EBFinishActivityMsg;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.modules.login.LoginActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.setFullScreen(this);
        setContentView(R.layout.activity_welcome);

        getPresenter(addFragment(WelcomeFragment.newInstance(), R.id.fragment));

        RuntimePermission.newInstance(this)
                .permission(Manifest.permission.READ_PHONE_STATE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .permission(Manifest.permission.CAMERA)
                .permission(Manifest.permission.RECORD_AUDIO)
                .onReject(this::showDialog)
                .onDialog(this::showDialog)
                .execute();

    }

    @Override
    public WelcomePresenter getPresenter(Fragment fragment) {

        return new WelcomePresenter((WelcomeContract.View) fragment);
    }

    private void showDialog(){
        new MaterialDialog.Builder(this)
                .title("提示")
                .titleColor(colorPrimary())
                .icon(ImageUtil.getDrawable(App.activity(),
                        R.drawable.ic_warning_24dp,colorPrimary()))
                .content("您已拒绝相关权限申请，功能无法正常使用，请到系统设置授权！")
                .positiveText("确定")
                .cancelable(false)
                .contentGravity(GravityEnum.CENTER)
                .positiveColor(colorPrimary())
                .onPositive((dialog, which) -> finish())
                .show();

    }

}
