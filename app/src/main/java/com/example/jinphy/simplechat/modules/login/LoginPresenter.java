package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.verification_code.CodeRepository;
import com.example.jinphy.simplechat.services.push.PushService;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;

import java.lang.ref.WeakReference;

/**
 *
 * Created by jinphy on 2017/8/10.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private WeakReference<Context> context;

    private CodeRepository codeRepository;

    private UserRepository userRepository;

    private FriendRepository friendRepository;

    private LoginContract.View view;

    public LoginPresenter(Context context, @NonNull LoginContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.context = new WeakReference<>(context);
        this.codeRepository = CodeRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
        this.friendRepository = FriendRepository.getInstance();
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
            userRepository.<User>newTask()
                    .param(Api.Key.account, phone)
                    // 开始时回调
                    .doOnStart(() -> view.enableCodeButton(false))
                    // 找到账号是回调
                    .doOnDataOk(user -> {
                        // 找到账号，获取验证码
                        codeRepository.<String>newTask()
                                .param(Api.Key.phone, phone)
                                .doOnDataOk(msg -> {
                                    App.showToast(msg.getMsg(), false);
                                    view.updateVerifiedAccount(phone);
                                    view.countDownCodeButton();
                                })
                                .doOnDataNo(reason -> view.enableCodeButton(true))
                                .submit(task -> codeRepository.getCode(context, task));
                    })
                    // 没有找到或者网络异常时回调，网络请求底层已做好了对应提示，我已这里无需再提示
                    .doOnDataNo(reason -> view.enableCodeButton(true))
                    // 提交设置并执行用户查找
                    .submit(task -> userRepository.findUser(context, task));
        }
    }

    @Override
    public void loginWithPassword(String account, String password, String deviceId) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            userRepository.<User>newTask()
                    .param(Api.Key.account,account)
                    .param(Api.Key.password,"null".equals(password)?password: EncryptUtils.md5(password))
                    .param(Api.Key.deviceId,deviceId)
                    // 登录成功时回调
                    .doOnDataOk(response -> {
                        User user = response.getData();
                        // 登录成功
                        view.whenLoginSucceed();
                        if (view.remenberPassword()) {
                            if (!"null".equals(password)) {
                                user.setPassword(EncryptUtils.aesEncrypt(password));
                            } else {
                                user.setPassword(null);
                            }
                            user.setRememberPassword(true);
                        } else {
                            user.setRememberPassword(false);
                        }
                        userRepository.saveUser(user);
                        friendRepository.addSystemFriendLocal(user.getAccount());
                    })
                    // 提交设置并执行登录操作
                    .submit(task -> userRepository.login(context, task));
        }
    }

    @Override
    public void loginWithCode(String account, String code, String deviceId) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();

            codeRepository.<String>newTask()
                    .param(Api.Key.phone, account)
                    .param(Api.Key.verificationCode, code)
                    // 验证码验证成功时回调
                    .doOnDataOk(msg -> {
                        // 验证码验证成功，执行登录操作
                        loginWithPassword(account, "null", deviceId);
                    })
                    // 提交设置并执行验证
                    .submit(task -> codeRepository.verify(context, task));
        }
    }

    @Override
    public User getUser() {
        return userRepository.currentUser();
    }
}
