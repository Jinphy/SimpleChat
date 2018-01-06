package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseRepository.Task;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.verification_code.CodeRepository;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by jinphy on 2017/8/10.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private WeakReference<Context> context;

    private CodeRepository codeRepository;

    private UserRepository userRepository;

    private LoginContract.View view;

    public LoginPresenter(Context context, @NonNull LoginContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.context = new WeakReference<>(context);
        this.codeRepository = CodeRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
    }
    @Override
    public void start() {

    }


    /**
     * DESC: 获取验证码
     * <p>
     * 分两步：
     * 1、查询用户是否存在
     * 2、如果账户存在则获取验证码
     * Created by jinphy, on 2018/1/6, at 16:06
     */
    @Override
    public void getVerificationCode(String phone) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            userRepository.newTask()
                    .param(Api.Key.account, phone)
                    // 开始时回调
                    .doOnStart(() -> view.enableCodeButton(false))
                    // 找到账号是回调
                    .doOnDataOk(user -> {
                        // 找到账号，获取验证码
                        codeRepository.newTask()
                                .param(Api.Key.phone, phone)
                                .doOnDataOk(msg -> {
                                    App.showToast(msg, false);
                                    view.updateVerifiedAccount(phone);
                                    view.countDownCodeButton();
                                })
                                .doOnDataNo(reason -> view.enableCodeButton(true))
                                .submit(builder -> codeRepository.getCode(context, builder));
                    })
                    // 没有找到或者网络异常时回调，网络请求底层已做好了对应提示，我已这里无需再提示
                    .doOnDataNo(reason -> view.enableCodeButton(true))
                    // 提交设置并执行用户查找
                    .submit(builder -> userRepository.findUser(context, builder));
        }
    }

    @Override
    public void loginWithPassword(String account, String password, String deviceId) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            userRepository.newTask()
                    .param(Api.Key.account,account)
                    .param(Api.Key.password,"null".equals(password)?password: EncryptUtils.md5(password))
                    .param(Api.Key.deviceId,deviceId)
                    // 登录成功时是回调
                    .doOnDataOk(user -> {
                        // 登录成功
                        view.whenLoginSucceed();
                        if (view.remenberPassword()) {
                            // TODO: 2018/1/6 保存账号
                        }
                        userRepository.saveUser(user);

                    })
                    // 提交设置并执行登录操作
                    .submit(builder -> userRepository.login(context, builder));
        }
    }

    @Override
    public void loginWithCode(String account, String code, String deviceId) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();

            codeRepository.newTask()
                    .param(Api.Key.phone, account)
                    .param(Api.Key.verificationCode, code)
                    // 验证码验证成功时回调
                    .doOnDataOk(msg -> {
                        // 验证码验证成功，执行登录操作
                        loginWithPassword(account, "null", deviceId);
                    })
                    // 提交设置并执行验证
                    .submit(builder -> codeRepository.verify(context, builder));
        }
    }
}
