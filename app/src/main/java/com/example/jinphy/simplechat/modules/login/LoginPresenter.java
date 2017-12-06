package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 *
 * Created by jinphy on 2017/8/10.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private NetworkManager networkManager;

    public LoginPresenter(@NonNull LoginContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        networkManager = NetworkManager.getInstance();

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
    public void findUser(Context context, String account, String hint, String tag) {
        Api.common(context)
                .param(Api.Key.account, account)
                .showProgress()
                .path(Api.Path.findUser)
                .onStart(()-> BaseApplication.showToast(hint, true))
                .onNext(response -> view.findUserOnNext(response,tag))
                .request();
    }

    @Override
    public void getVerificationCode(Context context, String phone) {
        Api.sms(context)
                .param(Api.Key.phone,phone)
                .showProgress()
                .path(Api.Path.getVerificationCode)
                .onNext(response -> view.getVerificationCodeOnNext(response, phone))
                .request();
    }


    @Override
    public void submitVerificationCode(Context context, String phone, String verificationCode,String hint) {
        Api.sms(context)
                .param(Api.Key.phone, phone)
                .param(Api.Key.verificationCode, verificationCode)
                .showProgress()
                .path(Api.Path.submitVerificationCode)
                .onStart(() -> BaseApplication.showToast(hint, true))
                .onNext(response -> view.submitVerificationCodeOnNext(response))
                .request();
    }

    @Override
    public void login(Context context, String account, String password, String deviceId) {
        Api.common(context)
                .param(Api.Key.account, account)
                .param(Api.Key.password, password)
                .param(Api.Key.deviceId, deviceId)
                .showProgress()
                .path(Api.Path.login)
                .onNext(response -> view.loginOnNext(response,account,password))
                .request();

    }
}
