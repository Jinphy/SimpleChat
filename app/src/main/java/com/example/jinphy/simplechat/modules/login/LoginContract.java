package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.jinphy.simplechat.api.Consumer;
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

        boolean isLoginByPassword();

    }

    interface Presenter extends BasePresenter{
        void registerSMSSDK(Context context);

        void  unregisterSMSSDK();

        void findUser(String account, Consumer callback);

        void getVerificationCode(String account, Consumer callback);


        void submitVerificationCode(String phone, String verificationCode, Consumer callback);

        void login(String account, String password,String deviceId, Consumer callback);
    }
}
