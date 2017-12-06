package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.application.DBApplication;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.model.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/9.
 */

public class SignUpPresenter implements SignUpContract.Presenter {

    private static final String TAG = "SignUpPresenter";
    private final SignUpContract.View view;
    private NetworkManager networkManager;
    private UserRepository userRepository;


    public SignUpPresenter(@NonNull SignUpContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.networkManager = ((DBApplication) DBApplication.instance()).getNetworkManager();
        this.userRepository = UserRepository.getInstance();
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
    public void getVerificationCode(Context context, String phone) {
        Api.sms(context)
                .param(Api.Key.phone, phone)
                .showProgress()
                .path(Api.Path.getVerificationCode)
                .onNext(view::getVerificationCodeOnNext)
                .request();
    }

    @Override
    public void submitVerificationCode(Context context, String phone, String verificationCode) {
//        smssdkApi.submitVerificationCode(phone, verificationCode,callback);
        Api.sms(context)
                .param(Api.Key.phone, phone)
                .param(Api.Key.verificationCode, verificationCode)
                .showProgress()
                .path(Api.Path.submitVerificationCode)
                .onNext(view::submitVerificationCodeOnNext)
                .request();
    }

    @Override
    public void findUser(Context context, String account) {
        Api.common(context)
                .param(Api.Key.account, account)
                .showProgress()
                .path(Api.Path.findUser)
                .onNext(view::findUserOnNext)
                .request();
    }

    @Override
    public void createNewUser(Context context,String account, String password,String date) {
        Api.common(context)
                .param(Api.Key.account, account)
                .param(Api.Key.password, password)
                .param(Api.Key.date, date)
                .showProgress()
                .path(Api.Path.createNewUser)
                .onStart(() -> BaseApplication.showToast("正在注册......", true))
                .onNext(response -> view.createNewUserOnNext(response, Long.valueOf(date)))
                .request();

    }

    @Override
    public User saveUser(String account, String password,long date) {
        // TODO: 2017/11/7
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setDate(date);
        userRepository.saveUser(user);
        return user;
    }


    @Override
    public void login(Context context, User user, String deviceId) {
        Api.common(context)
                .param(Api.Key.account, user.getAccount())
                .param(Api.Key.password, "null")
                .showProgress()
                .path(Api.Path.login)
                .onNext(response -> view.loginOnNext(response,user))
                .request();
    }

}
