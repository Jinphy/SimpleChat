package com.example.jinphy.simplechat.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

/**
 * 权限请求工具类
 * Created by Jinphy on 2017/11/23.
 */

public class PermissionUtils {

    private Runnable onGranted;
    private Runnable onReject;
    private Runnable onDialog;
    private List<String> permissions;
    private SoftReference<Activity> activity;
    private int grantedCount = 0;

    public static PermissionUtils getInstance(@NonNull Activity activity) {
        return new PermissionUtils(activity);
    }

    private PermissionUtils(@NonNull Activity activity) {
        this.activity = new SoftReference<>(activity);
        permissions = new LinkedList<>();
    }


    /**
     * 在权限被授予是回调
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public PermissionUtils onGrant(Runnable onGranted) {
        this.onGranted = onGranted;
        return this;
    }

    /**
     * 在权限被拒绝是回调
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public PermissionUtils onReject(Runnable onReject) {
        this.onReject = onReject;
        return this;
    }


    /**
     * 在权限被拒绝且不再弹框提示时回调，这时应该弹出对话框提示用户手动去系统设置里授予权限
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public PermissionUtils onDialog(Runnable onDialog) {
        this.onDialog = onDialog;
        return this;
    }


    /**
     * 添加权限
     * Created by Jinphy ,on 2017/11/23, at 21:06
     */
    public PermissionUtils permission(String permission) {
        this.permissions.add(permission);
        return this;
    }


    private static final String TAG = "PermissionUtils";

    /**
     * 执行权限申请
     * Created by Jinphy ,on 2017/11/23, at 21:06
     */
    public void execute() {
        if (permissions.size() == 0) {
            throw new RuntimeException("You must provide at lease one permission!");
        }
        Activity activity = this.activity.get();
        if (activity == null) {
            Log.e(TAG, "execute: activity is null");
        }
        String[] p = new String[permissions.size()];
        RxPermissions permission = new RxPermissions(activity);
        permission.requestEach(permissions.toArray(p))
                .subscribe(result -> { // will emit 2 Permission objects
                            if (result.granted && onGranted != null) {
                                // `permission.name` is granted !
                                if ((++grantedCount) == permissions.size()) {
                                    onGranted.run();
                                }
                            } else if (result.shouldShowRequestPermissionRationale && onDialog !=
                                    null) {
                                // Denied permission without ask never again
                                onDialog.run();
                            } else {
                                // Denied permission with ask never again
                                // Need to go to the settings
                                onReject.run();
                            }
                        }
                );
    }

}
