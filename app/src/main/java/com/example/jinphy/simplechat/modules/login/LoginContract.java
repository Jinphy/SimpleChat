package com.example.jinphy.simplechat.modules.login;

import android.content.Context;

import com.example.jinphy.simplechat.api.Api.Response;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter>{

        void showPasswordView();

        void showVerificationCodeView();

        void changeLoginType();

        String getAccount();

        String getPassword();

        String getCode();

        String getDeviceId();

        boolean isLoginByPassword();

        void findUserOnNext(Response response, String tag);

        void loginOnNext(Response response,String account,String password);

        void getVerificationCodeOnNext(Response response,String account);

        void submitVerificationCodeOnNext(Response response);
    }

    interface Presenter extends BasePresenter{
        void registerSMSSDK(Context context);

        void  unregisterSMSSDK();

        void findUser(Context context, String account, String hint, String tag);

        void getVerificationCode(Context context, String account);

        void submitVerificationCode(Context context, String phone, String verificationCode,String hint);

        void login(Context context, String account, String password, String deviceId);
    }
}
