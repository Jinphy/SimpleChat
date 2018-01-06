package com.example.jinphy.simplechat.modules.signup;

import android.widget.TextView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface SignUpContract {

    interface View extends BaseView<Presenter>{

        void getCodeSucceed(String msg);

        void getCodeFailed();

        void submitCodeSucceed(String msg);

        void submitCodeFailed();

        void findUserOk();

        void findUserNo(String reason);

        void whenSignUpSucceed(String msg);

        void whenLoginSucceed(String msg);
    }
    interface Presenter extends BasePresenter{

        void getVerificationCode(String phone);

        void submitVerificationCode(String phone, String verificationCode);

        void findUser(String account);

        void signUp(String account, String password, String date);

        void login(String account,String password, String deviceId);
    }
}
