package com.example.jinphy.simplechat.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateFriend;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class AppBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.example.jinphy.simplechat.AppReceiver";
    public static final String TAG = "TAG";
    public static final String MSG = "MSG";
    public static final String MESSAGE = "MESSAGE";
    public static final String LOGOUT = "LOGOUT";


    public static void send(Context context, String tag) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(TAG, tag);
        context.sendBroadcast(intent);
    }
    public static void send(Context context, String tag, String msg) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(TAG, tag);
        intent.putExtra(MSG, msg);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String tag = intent.getStringExtra(TAG);
        String msg = intent.getStringExtra(MSG);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        LogUtils.e(tag);
        switch (tag) {
            case MESSAGE:
                LogUtils.e(MESSAGE);
                // 新消息
                EventBus.getDefault().post(new EBUpdateView());
                break;
            case LOGOUT:
                new MaterialDialog.Builder(App.activity())
                        .title("警告")
                        .titleColor(App.activity().colorPrimary())
                        .icon(ImageUtil.getDrawable(App.activity(),
                                R.drawable.ic_warning_24dp, App.activity().colorPrimary()))
                        .content(msg)
                        .positiveText("确定")
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveColor(App.activity().colorPrimary())
                        .onPositive((dialog, which) -> {
                            UserRepository.getInstance().logoutLocal();
                        })
                        .show();
                break;
            default:
                break;
        }
    }
}
