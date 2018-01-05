package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 *
 * Created by jinphy on 2017/8/10.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;

    public LoginPresenter(@NonNull LoginContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }
    @Override
    public void start() {

    }


    @Override
    public void registerSMSSDK(Context context) {
//        smssdkApi.register(context);
    }

    @Override
    public void unregisterSMSSDK() {
//        smssdkApi.unregister();
    }

    @Override
    public void findUser(Context context, String account, String tag) {
        Api.common(context)
                .param(Api.Key.account, account)
                .showProgress()
                .path(Api.Path.findUser)
                .onResponseYes(response -> view.findUserOnNext(response,tag))
                .request();
    }

    @Override
    public void getVerificationCode(Context context, String phone) {
        Api.sms(context)
                .param(Api.Key.phone,phone)
                .hint("正在获取...")
                .showProgress()
                .path(Api.Path.getVerificationCode)
                .onResponseYes(response -> view.getVerificationCodeOnNext(response, phone))
                .onResponseNo(response -> BaseApplication.showToast(response.getMsg(),false))
                .request();
    }


    @Override
    public void submitVerificationCode(Context context, String phone, String verificationCode) {
        Api.sms(context)
                .param(Api.Key.phone, phone)
                .param(Api.Key.verificationCode, verificationCode)
                .hint("验证中...")
                .showProgress()
                .path(Api.Path.submitVerificationCode)
                .onResponseYes(response -> view.submitVerificationCodeOnNext(response))
                .onResponseNo(response -> BaseApplication.showToast(response.getMsg(),false))
                .request();
    }

    @Override
    public void login(Context context, String account, String password, String deviceId) {
        Api.<User>common(context)
                .param(Api.Key.account, account)
                .param(Api.Key.password, password)
                .param(Api.Key.deviceId, deviceId)
                .hint("正在登录...")
                .showProgress()
                .path(Api.Path.login)
                .dataType(Api.Data.MODEL,User.class)
                .onResponseYes(response -> {
                    User data = response.getData();
                    view.loginOnNext(response, account, password);
                })
                .request();

    }
}
