package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.widget.TextView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface SignUpContract {

    interface View extends BaseView<Presenter>{
        void showResultOfGetVerificationCode(String message);

        void logErrorMessageOfSMSSDK(Object data);

        void setText(TextView view, String text);

        void changeViewAfterSubmittingVerificationSuccessfully(String phone);

        void changeViewAfterGettingVerificationSuccessfully();

        void changeViewAfterSubmittingVerificationUnSuccessfully();

        void changeViewAfterGettingVerificationUnsuccessfully();
    }
    interface Presenter extends BasePresenter{
        void registerSMSSDK(Context context);

        void  unregisterSMSSDK();

        void getVerificationCode(String phone);

        void submitVerificationCode(String phone, String verificationCode);

    }
}
