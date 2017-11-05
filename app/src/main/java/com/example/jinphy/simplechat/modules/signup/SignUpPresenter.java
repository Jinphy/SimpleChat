package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.jinphy.simplechat.repositories.smssdk.SMSSDKRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import org.json.JSONObject;

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
        Log.d(TAG, "afterEvent: ================================");
        if (result == SMSSDK.RESULT_ERROR) {
            view.logErrorMessageOfSMSSDK(data);
            // 根据服务器返回的网络错误，给toast提示
            switch (event) {
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    view.showResultOfGetVerificationCode("获取验证码失败！");
                    break;
                default:
                    break;
            }

        } else {
            switch (event) {
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    view.showResultOfGetVerificationCode("验证码已发送，请正确输入！");
                    break;

                default:
                    break;
            }
        }
    }

}
