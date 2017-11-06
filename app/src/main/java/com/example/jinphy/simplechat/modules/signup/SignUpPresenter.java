package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jinphy.simplechat.repositories.smssdk.SMSSDKRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.smssdk.SMSSDK;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by jinphy on 2017/8/9.
 */

public class SignUpPresenter implements SignUpContract.Presenter,SMSSDKRepository.Callback {

    private static final String TAG = "SignUpPresenter";
    private final SignUpContract.View view;
    private SMSSDKRepository smssdkRepository;


    public SignUpPresenter(@NonNull SignUpContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.smssdkRepository = new SMSSDKRepository(this);
    }

    @Override
    public void start() {

    }



    @Override
    public void registerSMSSDK(Context context) {
        smssdkRepository.register(context);
    }

    @Override
    public void unregisterSMSSDK() {
        smssdkRepository.unregister();
    }

    @Override
    public void getVerificationCode(String phone) {
        smssdkRepository.getVerificationCode(phone);
    }

    @Override
    public void submitVerificationCode(String phone, String verificationCode) {
        smssdkRepository.submitVerificationCode(phone, verificationCode);
    }

    @Override
    public void afterEvent(int event, int result, Object data) {
        Flowable.just("start")
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(value -> {
                    if (result == SMSSDK.RESULT_ERROR) {
                        view.logErrorMessageOfSMSSDK(data);
                        // 根据服务器返回的网络错误，给toast提示
                        switch (event) {
                            case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                                view.showResultOfGetVerificationCode("获取验证码失败！");
                                view.changeViewAfterGettingVerificationUnsuccessfully();
                                break;
                            case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                                view.showResultOfGetVerificationCode("验证失败，请输入正确的验证码！");
                                view.changeViewAfterSubmittingVerificationUnSuccessfully();
                            default:
                                break;
                        }

                    } else {
                        switch (event) {
                            case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                                view.showResultOfGetVerificationCode("验证码已发送，请正确输入！");
                                view.changeViewAfterGettingVerificationSuccessfully();
                                break;
                            case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                                view.showResultOfGetVerificationCode("已验证，请继续！");
                                // TODO: 2017/11/5
                                HashMap<String, String> map = (HashMap<String, String>) data;
//                                String country = map.get("country");
                                String phone = map.get("phone");
                                view.changeViewAfterSubmittingVerificationSuccessfully(phone);
                                break;
                            default:
                                break;
                        }
                    }

                })
                .subscribe();

    }

}
