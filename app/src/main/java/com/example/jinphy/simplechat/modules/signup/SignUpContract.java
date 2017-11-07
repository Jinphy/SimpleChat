package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Consumer;
import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.user.User;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface SignUpContract {

    interface View extends BaseView<Presenter>{

        void setText(TextView view, String text);

        void updateViewAfterSubmittingVerificationCode(Response response);

        void updateViewAfterGettingVerificationCode(Response response);

        void updateViewAfterCreateNewUser(Response response,long date);

        void updateViewAfterFindUser(Response response);
    }
    interface Presenter extends BasePresenter{
        void registerSMSSDK(Context context);

        void  unregisterSMSSDK();

        void getVerificationCode(String phone,Consumer callback);

        void submitVerificationCode(String phone, String verificationCode,Consumer callback);

        void findUser(String account, Consumer callback);

        void createNewUser(String account, String password,String date, Consumer callback);

        User saveUser(String account, String password,long date);
    }
}
