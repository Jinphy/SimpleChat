package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.widget.TextView;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.user.User;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface SignUpContract {

    interface View extends BaseView<Presenter>{

        void setText(TextView view, String text);

        void getVerificationCodeOnNext(Api.Response response);

        void submitVerificationCodeOnNext(Api.Response response);

        void findUserOnNext(Api.Response response);

        void createNewUserOnNext(Api.Response response, long date);

        void loginOnNext(Api.Response response, User user);
//
//        void updateViewAfterSubmittingVerificationCode(Api.Response response);
//
//        void updateViewAfterGettingVerificationCode(Api.Response response);
//
//        void updateViewAfterCreateNewUser(Api.Response response, long date);
//
//        void updateViewAfterFindUser(Api.Response response);
    }
    interface Presenter extends BasePresenter{
        void registerSMSSDK(Context context);

        void  unregisterSMSSDK();

        void getVerificationCode(Context context, String phone);

        void submitVerificationCode(Context context, String phone, String verificationCode);

        void findUser(Context context, String account);

        void createNewUser(Context context, String account, String password, String date);

        User saveUser(String account, String password,long date);

        void login(Context context, User user, String deviceId);
    }
}
