package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.api.Consumer;
import com.example.jinphy.simplechat.api.NetworkManager;
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
    public void findUser(String account, Consumer callback) {
        networkManager.findUser(account,callback);
    }

    @Override
    public void getVerificationCode(String account, Consumer callback) {
        smssdkApi.getVerificationCode(account, callback);
    }


    @Override
    public void submitVerificationCode(String phone, String verificationCode,Consumer callback) {
        smssdkApi.submitVerificationCode(phone, verificationCode,callback);
    }

    @Override
    public void login(String account, String password, String deviceId,Consumer callback) {
        networkManager.login(account, password, deviceId,callback);
    }
}
