package com.example.jinphy.simplechat.modules.login;

import android.content.Context;

import com.example.jinphy.simplechat.api.Response;
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

        void enableCodeButton(boolean enable);

        void countDownCodeButton();

        void updateVerifiedAccount(String verifiedAccount);

        void whenLoginSucceed();

        String getAccount();

        String getPassword();

        String getCode();

        boolean isLoginByPassword();

        boolean remenberPassword();
    }

    interface Presenter extends BasePresenter{

        void getVerificationCode(String phone);

        void loginWithPassword(String account, String password, String deviceId);

        void loginWithCode(String account, String code, String deviceId);
    }
}
