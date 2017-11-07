package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jinphy.simplechat.api.Consumer;
import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.api.SMSSDKApi;
import com.example.jinphy.simplechat.application.DBApplication;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.model.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.HashMap;

import cn.smssdk.SMSSDK;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by jinphy on 2017/8/9.
 */

public class SignUpPresenter implements SignUpContract.Presenter {

    private static final String TAG = "SignUpPresenter";
    private final SignUpContract.View view;
    private SMSSDKApi smssdkApi;
    private NetworkManager networkManager;
    private UserRepository userRepository;


    public SignUpPresenter(@NonNull SignUpContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.smssdkApi = SMSSDKApi.getInstance();
        this.networkManager = ((DBApplication) DBApplication.INSTANCE).getNetworkManager();
        this.userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }



    @Override
    public void registerSMSSDK(Context context) {
        smssdkApi.register(context);
    }

    @Override
    public void unregisterSMSSDK() {
        smssdkApi.unregister();
    }

    @Override
    public void getVerificationCode(String phone,Consumer callback) {
        smssdkApi.getVerificationCode(phone,callback);
    }

    @Override
    public void submitVerificationCode(String phone, String verificationCode,Consumer callback) {
        smssdkApi.submitVerificationCode(phone, verificationCode,callback);
    }

    @Override
    public void findUser(String account, Consumer callback) {
        networkManager.findUser(account,callback);
    }

    @Override
    public void createNewUser(String account, String password,String date, Consumer callback) {
        networkManager.createNewUser(account,password,date,callback);
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



}
