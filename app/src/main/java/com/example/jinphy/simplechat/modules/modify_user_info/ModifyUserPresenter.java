package com.example.jinphy.simplechat.modules.modify_user_info;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.models.verification_code.CodeRepository;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * DESC: 修改用户信息presenter
 * Created by jinphy on 2018/1/7.
 */

public class ModifyUserPresenter implements ModifyUserContract.Presenter{

    private WeakReference<Context> context;

    private UserRepository userRepository;

    private CodeRepository codeRepository;

    private final ModifyUserContract.View view;


    public ModifyUserPresenter(Context context, ModifyUserContract.View view) {
        this.context = new WeakReference<>(context);
        this.view = view;
        this.userRepository = UserRepository.getInstance();
        this.codeRepository = CodeRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public User getUser() {
        return userRepository.currentUser();
    }


    @Override
    public void modifyUserInfo(Context context, Map<String, Object> params) {
        userRepository.<Map<String,String>>newTask(params)
                .doOnDataOk(response -> {
                    String passwordAES = view.getModifiedPasswordAES();
                    if (passwordAES != null) {
                        response.getData().put(User_.password.name, passwordAES);
                    }
                    userRepository.updateUser(response.getData());
                    view.whenModifyUserInfoSucceed();
                })
                .doOnDataNo(reason -> {
                    //no-op
                })
                .submit(builder -> userRepository.modifyUserInfo(context, builder));
    }

    @Override
    public void getCode(Context context, String phone,BaseRepository.OnDataOk<Response<String>> onDataOk) {
        codeRepository.<String>newTask()
                .param(Api.Key.phone,phone)
                .doOnDataOk(onDataOk)
                .submit(task -> codeRepository.getCode(context, task));
    }

    @Override
    public void submitCode(Context context, String phone, String code, BaseRepository.OnDataOk<Response<String>> onDataOk) {
        codeRepository.<String>newTask()
                .param(Api.Key.phone, phone)
                .param(Api.Key.verificationCode, code)
                // 验证码验证成功时回调
                .doOnDataOk(onDataOk)
                // 提交设置并执行验证
                .submit(task -> codeRepository.verify(context, task));
    }

}

