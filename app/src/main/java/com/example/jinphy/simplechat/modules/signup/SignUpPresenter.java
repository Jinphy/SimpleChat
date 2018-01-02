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
        this.networkManager = ((DBApplication) DBApplication.app()).getNetworkManager();
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
                .hint("正在获取...")
                .showProgress()
                .path(Api.Path.getVerificationCode)
                .onResponseYes(view::getVerificationCodeOnNext)
                .request();
    }

    @Override
    public void submitVerificationCode(Context context, String phone, String verificationCode) {
//        smssdkApi.submitVerificationCode(phone, verificationCode,callback);
        Api.sms(context)
                .param(Api.Key.phone, phone)
                .param(Api.Key.verificationCode, verificationCode)
                .hint("验证中...")
                .showProgress()
                .path(Api.Path.submitVerificationCode)
                .onResponseYes(view::submitVerificationCodeOnNext)
                .request();
    }

    @Override
    public void findUser(Context context, String account) {
        Api.common(context)
                .param(Api.Key.account, account)
                .showProgress()
                .path(Api.Path.findUser)
                .autoShowNo(false)
                .onResponseYes(response -> BaseApplication.showToast(response.getMsg(), false))
                .onResponseNo(view::findUserOnNext)
                .request();
    }

    @Override
    public void createNewUser(Context context,String account, String password,String date) {
        Api.common(context)
                .param(Api.Key.account, account)
                .param(Api.Key.password, password)
                .param(Api.Key.date, date)
                .hint("正在注册...")
                .showProgress()
                .path(Api.Path.createNewUser)
                .onResponseYes(response -> view.createNewUserOnNext(response, Long.valueOf(date)))
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
                .hint("正在登录...")
                .showProgress()
                .path(Api.Path.login)
                .onResponseYes(response -> view.loginOnNext(response,user))
                .request();
    }

}
