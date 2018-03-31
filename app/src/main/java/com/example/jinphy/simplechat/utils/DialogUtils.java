package com.example.jinphy.simplechat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.user.UserRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        if (context == null) {
            return;
        }
        int red = ContextCompat.getColor(context, R.color.color_red_D50000);
        CharSequence content = responseNo.getMsg();
        SChain sChain = SChain.with(content).sizeRelative(0.85f);
        MaterialDialog.SingleButtonCallback onPositiveClick;
        switch (responseNo.getCode()) {
            case Response.NO_FIND_USER:
                sChain.colorForText(red,2,13);
                break;
            default:
                break;
        }
        boolean accountUsable = !Response.NO_ACCESS_TOKEN.equals(responseNo.getCode());
        new MaterialDialog.Builder(context)
                .title(SChain.with("错误").sizeRelative(0.8f).make())
                .iconRes(R.drawable.ic_cry_red_24dp)
                .content(sChain.make())
                .contentGravity(GravityEnum.CENTER)
                .positiveText("确定")
                .titleColorRes(R.color.color_red_D50000)
                .positiveColorRes(R.color.color_red_D50000)
                .cancelable(accountUsable)
                .onPositive((dialog, which) -> {
                    if (!accountUsable) {
                        UserRepository.getInstance().logoutLocal();
                    }
                })
                .show();
    }

    /**
     * DESC: 显示提示对话框
     * Created by jinphy, on 2018/3/30, at 18:22
     */
    public static MyDialog.Holder showHint(Context context, String hint) {
        MyDialog.Holder holder = MyDialog.create(context)
                .view(R.layout.layout_hint_dialog)
                .cancelable(false)
                .y(-30)
                .display();
        if (hint != null) {
            ((TextView) holder.view.findViewById(R.id.hint_view)).setText(hint);
        }
        return holder;
    }

    /**
     * DESC: 显示二维码对话框
     * Created by jinphy, on 2018/3/30, at 18:22
     */
    public static void showQRCode(Context context,
                           String content,
                           String account,
                           String name,
                           String hint) {

        MyDialog.Holder hintDialogHolder = DialogUtils.showHint(context, null);

        Bitmap logo = ImageUtil.loadAvatar(account, 100, 100);

        Observable.just("")
                .subscribeOn(Schedulers.computation())
                .map(v -> QRCode.from(content).logo(logo).make())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(qrCode -> {
                    hintDialogHolder.dialog.dismiss();
                    MyDialog.Holder dialogHolder = MyDialog.create(context)
                            .width(330)
                            .view(R.layout.layout_qr_code_dialog)
                            .display();
                    dialogHolder.view.setOnClickListener(v -> {
                        dialogHolder.dialog.dismiss();
                    });
                    TextView nameView = dialogHolder.view.findViewById(R.id.name_view);
                    TextView accountView = dialogHolder.view.findViewById(R.id.account_view);
                    ImageView avatarView = dialogHolder.view.findViewById(R.id.avatar_view);
                    ImageView qrCodeView = dialogHolder.view.findViewById(R.id.qr_code_view);
                    TextView hintView = dialogHolder.view.findViewById(R.id.hint_view);
                    if (name != null) {
                        nameView.setText(name);
                    }
                    if (account != null) {
                        accountView.setText(account);
                    }
                    if (hint != null) {
                        hintView.setText(hint);
                    }
                    if (logo != null) {
                        avatarView.setImageBitmap(logo);
                    }
                    qrCodeView.setImageBitmap(qrCode);
                })
                .subscribe();
    }
}
