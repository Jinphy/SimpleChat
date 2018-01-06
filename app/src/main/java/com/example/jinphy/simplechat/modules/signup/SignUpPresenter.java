package com.example.jinphy.simplechat.modules.signup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.verification_code.CodeRepository;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.lang.ref.WeakReference;

/**
 * Created by jinphy on 2017/8/9.
 */

public class SignUpPresenter implements SignUpContract.Presenter {

    private static final String TAG = "SignUpPresenter";
    private final SignUpContract.View view;
    private UserRepository userRepository;
    private CodeRepository codeRepository;
    private WeakReference<Context> context;


    public SignUpPresenter(Context context, @NonNull SignUpContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.userRepository = UserRepository.getInstance();
        this.codeRepository = CodeRepository.getInstance();
        this.context = new WeakReference<>(context);
    }


    @Override
    public void start() {

    }


    /**
     * DESC: 获取验证
     * Created by jinphy, on 2018/1/6, at 19:26
     */
    @Override
    public void getVerificationCode(String phone) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            codeRepository.newTask()
                    .param(Api.Key.phone, phone)
                    // 找到账号是回调
                    .doOnDataOk(view::getCodeSucceed)
                    // 没有找到或者网络异常时回调，网络请求底层已做好了对应提示，我已这里无需再提示
                    .doOnDataNo(reason -> view.getCodeFailed())
                    // 提交设置并执行用户查找
                    .submit(builder -> codeRepository.getCode(context, builder));
        }
    }

    /**
     * DESC: 提交验证码
     * Created by jinphy, on 2018/1/6, at 19:25
     */
    @Override
    public void submitVerificationCode(String phone, String verificationCode) {
            if (ObjectHelper.reference(context)) {
                Context context = this.context.get();
                codeRepository.newTask()
                        .param(Api.Key.phone, phone)
                        .param(Api.Key.verificationCode, verificationCode)
                        // 验证码验证成功时回调
                        .doOnDataOk(view::submitCodeSucceed)
                        .doOnDataNo(reason -> view.submitCodeFailed())
                        // 提交设置并执行验证
                        .submit(builder -> codeRepository.verify(context, builder));
            }
    }

    @Override
    public void findUser(String account) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            userRepository.newTask()
                    .param(Api.Key.account, account)
                    .autoShowNo(false)
                    // 找到账号是回调
                    .doOnDataOk(user -> view.findUserOk())
                    // 没有找到或者网络异常时回调，网络请求底层已做好了对应提示，我已这里无需再提示
                    .doOnDataNo(view::findUserNo)
                    // 提交设置并执行用户查找
                    .submit(builder -> userRepository.findUser(context, builder));
        }
    }

    @Override
    public void signUp(String account, String password, String date) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();
            userRepository.newTask()
                    .param(Api.Key.account, account)
                    .param(Api.Key.password, EncryptUtils.md5(password))
                    .param(Api.Key.date, date)
                    // 注册成功时回调
                    .doOnDataOk(user -> view.whenSignUpSucceed("账号注册成功！"))
                    // 没有找到或者网络异常时回调，网络请求底层已做好了对应提示，我已这里无需再提示
                    .doOnDataNo(view::findUserNo)
                    // 提交设置并执行用户查找
                    .submit(builder -> userRepository.signUp(context, builder));

        }
    }


    @Override
    public void login(String account,String password, String deviceId) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();

            userRepository.newTask()
                    .param(Api.Key.account, account)
                    .param(Api.Key.password, EncryptUtils.md5(password))
                    .param(Api.Key.deviceId, deviceId)
                    // 登录成功时回调
                    .doOnDataOk(user -> {
                        view.whenLoginSucceed("登录成功！");
                        // TODO: 2018/1/6 保存账号
                    })
                    // 提交设置并执行登录操作
                    .submit(builder -> userRepository.login(context, builder));
        }

    }

}
