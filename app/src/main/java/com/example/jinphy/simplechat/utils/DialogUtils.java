package com.example.jinphy.simplechat.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.custom_libs.SChain;

/**
 * DESC:
 * Created by jinphy on 2018/1/2.
 */

public class DialogUtils {


    /**
     * DESC: 显示网络请求返回码错误时的对话框
     * Created by jinphy, on 2018/1/2, at 18:09
     */
    public static void showResponseNo(Response responseNo, Context context) {
        int red = ContextCompat.getColor(context, R.color.color_red_D50000);
        CharSequence content = responseNo.getMsg();
        MaterialDialog.SingleButtonCallback onPositiveClick;
        switch (responseNo.getCode()) {
            case Response.NO_FIND_USER:
                content = SChain.with(content).colorForText(red,2,13).make();
                break;
            default:
                break;
        }
        new MaterialDialog.Builder(context)
                .title("错误")
                .iconRes(R.drawable.ic_cry_red_24dp)
                .content(content)
                .positiveText("确定")
                .titleColorRes(R.color.color_red_D50000)
                .positiveColorRes(R.color.color_red_D50000)
                .cancelable(true)
                .onPositive((dialog, which) -> {

                })
                .show();

    }
}
