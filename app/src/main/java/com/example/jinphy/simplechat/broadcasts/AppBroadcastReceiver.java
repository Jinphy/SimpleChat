package com.example.jinphy.simplechat.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.models.event_bus.EBFileTask;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.event_bus.EBSendMsg;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.modules.chat.FileListener;
import com.example.jinphy.simplechat.modules.login.LoginActivity;
import com.example.jinphy.simplechat.modules.signup.SignUpActivity;
import com.example.jinphy.simplechat.modules.welcome.WelcomeActivity;
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
    public static final String TAG_UPLOAD_FILE = "TAG_UPLOAD_FILE";
    public static final String TAG_DOWNLOAD_PHOTO = "TAG_DOWNLOAD_PHOTO";
    public static final String TAG_SEND_MSG = "TAG_SEND_MSG";
    public static final String TAG_DOWNLOAD_VOICE = "TAG_DOWNLOAD_VOICE";
    public static final String TAG_DOWNLOAD_FILE = "TAG_DOWNLOAD_FILE";


    private static FileListener uploadFileListener;

    private static FileListener downloadFileListener;


    public static void send(Context context, String tag) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(ACTION);
        intent.putExtra(TAG, tag);
        context.sendBroadcast(intent);
    }
    public static void send(Context context, String tag, String msg) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(ACTION);
        intent.putExtra(TAG, tag);
        intent.putExtra(MSG, msg);
        context.sendBroadcast(intent);
    }

    public static void registerUploadFileListener(FileListener listener) {
        uploadFileListener = listener;
    }

    public static void unregisterUploadFileListener() {
        uploadFileListener = null;
    }

    public static void registerDownloadFileListener(FileListener listener) {
        downloadFileListener = listener;
    }

    public static void unregisterDownloadFileListener() {
        downloadFileListener = null;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String tag = intent.getStringExtra(TAG);
        String msg = intent.getStringExtra(MSG);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        switch (tag) {
            case MESSAGE:
                // 新消息
                EventBus.getDefault().post(new EBUpdateView());
                break;
            case LOGOUT:
                BaseActivity currentActivity = App.activity();
                if (currentActivity == null
                        || currentActivity.getClass() == LoginActivity.class
                        || currentActivity.getClass() == SignUpActivity.class
                        || currentActivity.getClass() == WelcomeActivity.class) {
                    return;
                }
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
            case TAG_UPLOAD_FILE:
                onFileEvent(msg, uploadFileListener);
                break;
            case TAG_DOWNLOAD_PHOTO:
                onFileEvent(msg, downloadFileListener);
                break;
            case TAG_SEND_MSG:{
                String[] split = msg.split(":");
                EventBus.getDefault().post(EBSendMsg.make(split[0], split[1]));
                break;
            }
            case TAG_DOWNLOAD_VOICE:{
                String[] split = msg.split(":");
                EventBus.getDefault().post(
                        EBMessage.downloadVoiceResult(split[0], Long.valueOf(split[1])));
                break;
            }
            case TAG_DOWNLOAD_FILE:{
                String[] split = msg.split(":");
                EBFileTask ebMsg = new EBFileTask(split[0]);
                ebMsg.msgId = Long.valueOf(split[1]);
                if ("onUpdate".equals(ebMsg.data)) {
                    ebMsg.percent = (int) (Double.valueOf(split[2]) / Double.valueOf(split[3]) *
                            100);
                } else {
                    EventBus.getDefault().post(EBMessage.reloadMsg(ebMsg.msgId));
                }
                EventBus.getDefault().post(ebMsg);
                break;
            }
            default:
                break;
        }
    }

    private void onFileEvent(String msg, FileListener listener) {
        String[] split = msg.split(":");
        if (listener == null) {
            return;
        }
        switch (split[0]) {
            case "onStart":
                listener.onStart(Long.valueOf(split[1]));
                break;
            case "onError":
                listener.onError(Long.valueOf(split[1]));
                break;
            case "onUpdate":
                listener.onUpdate(
                        Long.valueOf(split[1]),
                        Long.valueOf(split[2]),
                        Long.valueOf(split[3]));
                break;
            case "onFinish":
                listener.onFinish(Long.valueOf(split[1]));
                break;
        }
    }
}
