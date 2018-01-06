package com.example.jinphy.simplechat.custom_libs;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 6.0 及以上版本运行时权限申请类
 *
 * <h1>使用说明：</h1>
 *  <h2>1、实例代码：</h2>
 *     <p>{@code
 *     RuntimePermission.getInstance(activity)
 *                         .permission(Manifest.permission.READ_PHONE_STATE)
 *                         .onGranted(() -> {
 *                              // 用户同意了所申请的权限，接下来可以执行具体的逻辑
 *                         })
 *                         .onReject(()->{
 *                             // 用户拒绝了权限申请，不能执行与所申请的权限有关的功能
 *                         })
 *                         .onDialog(()->{
 *                             // 用户拒绝了权限申请，但是可能是以前用户选了不再提示，此时，应该弹出一个对话框引导用户手动到系统设置里开启权限
 *                         })
 *                         .execute();
 *     }</p>
 *  <h2>2、注意事项：</h2>
 *      你可以使用方法{@link RuntimePermission#permission(String...)} 来添加你需要申请的权限，如果要同时申请多条权限，你有两种选择
 *
 *       1、多次调用{@code permission(String...)}，如果使用这个方式，建议每次传入一条权限，可读性较好
 *
 *       2、调用一次{@code permission(String...)}， 传入多条权限，以都好隔开，建议每条权限分别写在一行，例如<p>{@code
 *              .permission(Manifest.permission.READ_PHONE_STATE,
 *                          Manifest.permission.READ_WRITE_EXTERNAL_STORAGE,
 *                          Manifest.permission.CAMERA)
 *       }</p>
 *
 *
 *
 * Created by Jinphy on 2017/11/23.
 */

public class RuntimePermission {

    private Runnable onGranted;
    private Runnable onReject;
    private Runnable onDialog;
    private List<String> permissions;
    private SoftReference<Activity> activity;
    private int grantedCount = 0;

    public static RuntimePermission getInstance(@NonNull Activity activity) {
        return new RuntimePermission(activity);
    }

    private RuntimePermission(@NonNull Activity activity) {
        this.activity = new SoftReference<>(activity);
        permissions = new LinkedList<>();
    }


    /**
     * 请求成功是回调
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public RuntimePermission onGranted(Runnable onGranted) {
        this.onGranted = onGranted;
        return this;
    }

    /**
     * 请求拒绝时回调
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public RuntimePermission onReject(Runnable onReject) {
        this.onReject = onReject;
        return this;
    }


    /**
     * 请求拒绝并且需要弹框时回调
     * 此时应该显示弹框让用户手动开启权限
     * Created by Jinphy ,on 2017/11/23, at 21:07
     */
    public RuntimePermission onDialog(Runnable onDialog) {
        this.onDialog = onDialog;
        return this;
    }


    /**
     * 添加请求权限,可一次添加多条
     * Created by Jinphy ,on 2017/11/23, at 21:06
     */
    public RuntimePermission permission(String...permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    private static final String TAG = "RuntimePermission";

    /**
     * 执行权限申请
     * Created by Jinphy ,on 2017/11/23, at 21:06
     */
    public void execute() {
        if (permissions.size() == 0) {
            throw new RuntimeException("You must provide at lease one permission!");
        }
        if (!ObjectHelper.reference(this.activity)) {
            return;
        }
        Activity activity = this.activity.get();
        String[] p = new String[permissions.size()];
        RxPermissions permission = new RxPermissions(activity);
        permission.requestEach(permissions.toArray(p))
                .subscribe(result -> { // will emit 2 RuntimePermission objects
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
                });
    }

}
