package com.example.jinphy.simplechat.modules.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.StringConst;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.model.event_bus.EBFinishActivity;
import com.example.jinphy.simplechat.model.event_bus.EBLoginInfo;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.modules.main.MainActivity;
import com.example.jinphy.simplechat.modules.signup.SignUpActivity;
import com.example.jinphy.simplechat.modules.welcome.WelcomeActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.Encrypt;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends BaseFragment<LoginPresenter> implements LoginContract.View{

    public static final int TYPE_PASSWORD = 0;
    public static final int TYPE_VERIFICATION_CODE = 1;
    private static final String TAG = "LoginFragment";

    protected TextInputLayout accountLayout;
    protected TextInputLayout passwordLayout;
    protected TextInputLayout verificationCodeLayout;
    protected View verificationCodeView;
    protected TextView getVerificationCodeButton;
    protected View gotoSignUpView;
    protected View rememberPasswordView;
    protected CheckBox rememberMeBox;
    protected TextView loginTypeButton;
    protected TextView loginButton;

    protected String verifiedAccount = null;

    //------------fragment 的函数-----------------------------------

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }
        presenter.registerSMSSDK(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unregisterSMSSDK();
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_login;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        accountLayout = view.findViewById(R.id.account_layout);
        passwordLayout = view.findViewById(R.id.password_layout);
        verificationCodeLayout = view.findViewById(R.id.verification_code_layout);
        verificationCodeView = view.findViewById(R.id.verification_code_view);
        getVerificationCodeButton = view.findViewById(R.id.get_verification_code_button);
        gotoSignUpView = view.findViewById(R.id.goto_sign_up_text);
        rememberPasswordView = view.findViewById(R.id.remember_password_layout);
        rememberMeBox = view.findViewById(R.id.remember_password_checkbox);
        loginTypeButton = view.findViewById(R.id.login_type_button);
        loginButton = view.findViewById(R.id.login_button);
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void registerEvent() {
        gotoSignUpView.setOnClickListener(this::onClick);
        getVerificationCodeButton.setOnClickListener(this::onClick);
        loginTypeButton.setOnClickListener(this::onClick);
        loginButton.setOnClickListener(this::onClick);
        rememberPasswordView.setOnClickListener(this::onClick);

        TextInputEditText editText = accountLayout.findViewById(R.id.account_text);
        editText.addTextChangedListener(onTextChanged());
    }

    //------------私有函数------------------------------------------

    //==================事件listener函数========================
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.goto_sign_up_text:
//                跳转到注册界面
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
                break;
            case R.id.get_verification_code_button:
//                获取验证码,先检查当前用户是否存在，如果存在则获取验证码
                getVerificationCode();
                break;
            case R.id.login_type_button:
//                更换登录方式
                changeLoginType();
                break;
            case R.id.login_button:
//                登录
                doLogin();
                break;
            case R.id.remember_password_layout:
//                包含checkbox 和 文本的layout的点击事件
                rememberMeBox.setChecked(!rememberMeBox.isChecked());
            default:
                break;
        }
    }


    private TextWatcherAdapter onTextChanged() {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                checkAccount(text, text.length());
            }
        };
    }

    //==============普通函数=====================================

    private synchronized void getVerificationCode(){
        String account = getAccount();
        if (StringUtils.equal(verifiedAccount, account)) {
            BaseApplication.showToast("该账号已经验证过，无需再验证！", false);
            return;
        }
        getVerificationCodeButton.setEnabled(false);
        presenter.findUser(account, findResponse -> {
            // 查询用户是否存在
            handleResponse(findResponse,
                    null,
                    "当前账号不存在，请重新输入！",
                    "服务异常，请稍后重试！",
                    false
            );
            if (Response.yes.equals(findResponse.message)) {
                // 用户存在，则为当前用户获取验证码
                Flowable.just("正在获取验证码，请稍等！")
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(msg -> BaseApplication.showToast(msg, false))
                        .subscribe();
                presenter.getVerificationCode(account, getResponse->{
                    handleResponse(
                            getResponse,
                            "验证码已发送，请正确输入！",
                            "获取验证码失败!",
                            null,
                            true);
                    if (Response.yes.equals(getResponse.message)) {
                        verifiedAccount = account;
                        String origin = getVerificationCodeButton.getText().toString();
                        String text = BaseApplication.instance().getString(R.string.re_get);
                        Flowable.intervalRange(1, 60, 0, 1, TimeUnit.SECONDS)
                                .map(value -> {
                                    value = 60 - value;
                                    return text.replace(" ", value + "");
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(value -> getVerificationCodeButton.setText(value))
                                .doOnComplete(() -> {
                                    getVerificationCodeButton.setEnabled(true);
                                    getVerificationCodeButton.setText(origin);
                                })
                                .subscribe();
                    } else {
                        getActivity().runOnUiThread(()-> getVerificationCodeButton.setEnabled(true));
                    }
                });
            }else {
                getActivity().runOnUiThread(()-> getVerificationCodeButton.setEnabled(true));
            }
        });

    }

    private synchronized void doLogin() {
        String account = getText(accountLayout,R.id.account_text);
        String verificationCode = getText(verificationCodeLayout, R.id.verification_code_text);
        String password = getText(passwordLayout,R.id.password_text);
        String deviceId = Encrypt.md5(DeviceUtils.devceId());

//        判断账号格式是否合法
        if (!checkAccount(account, account.length())) {
            BaseApplication.showToast("请输入有效的账户！", false);
            return;
        }

//        判断登录方式
        if (isLoginByPassword()) {
            doLoginByPassword(account,password,deviceId);
        } else {
//            验证码方式登录
            doLoginByVerificationCode(account, verificationCode,deviceId);
        }
    }

    //            密码方式登录
    private void doLoginByPassword(String account, String password,String deviceId) {
        if (password.length() == 0) {
            BaseApplication.showToast("请输入密码！",true);
            return;
        }

        String encryptedPassword = Encrypt.md5(password);
        presenter.findUser(account,findResponse -> {
            handleResponse(
                    findResponse,
                    null,
                    "用户不存在！",
                    "服务器异常，请稍后再试！",
                    false
            );
            if (Response.yes.equals(findResponse.message)) {
                presenter.login(account,encryptedPassword,deviceId,loginResponse -> {
                    handleResponse(
                            loginResponse,
                            "登录成功！",
                            "密码错误，请重新输入",
                            "服务器错误，请稍后再试！",
                            false
                    );
                    doAfterLogin(loginResponse, account, password);
                });
            }
        });
    }

    private void doLoginByVerificationCode(String account, String verificationCode,String deviceId) {
        if (StringUtils.equal(account, verifiedAccount)) {
            presenter.submitVerificationCode(account,verificationCode,submitResponse -> {
                handleResponse(
                        submitResponse,
                        "已验证，正在登录...",
                        "验证码不正确，请重新输入！",
                        "服务器异常，请稍后再试！",
                        false
                );
                if (Response.yes.equals(submitResponse.message)) {
                    presenter.login(account,"null",deviceId,loginResponse -> {
                        handleResponse(
                                loginResponse,
                                "登录成功！",
                                "密码错误，请重新输入",
                                "服务器错误，请稍后再试！",
                                false
                        );
                        doAfterLogin(loginResponse, account, null);
                    });
                }

            });
        } else {
            BaseApplication.showToast("请先验证账号！", false);
        }
    }

    private void doAfterLogin(Response response,String account,String password) {
        if (Response.yes.equals(response.message)) {
            // 登录成功
            // 发送给MainActivity
            boolean rememberPassword = rememberMeBox.isChecked();
            User user = new User();
            user.setAccount(account);
            user.setPassword(password);
            MainActivity.start(getActivity(),user, rememberPassword);
            EventBus.getDefault().post(new EBFinishActivity(WelcomeActivity.class));
            finishActivity();
        }
    }

    private String getText(View container, @IdRes int id) {
        EditText editText = container.findViewById(id);
        return editText.getText().toString();
    }


    //-----------------EventBus---------------------------------



    //----------mvp中View的函数--------------------------------------


    @Override
    public void showPasswordView() {
        animateView(passwordLayout,verificationCodeView);
        loginTypeButton.setText(R.string.verification_type);
    }

    @Override
    public void showVerificationCodeView() {
        animateView(verificationCodeView,passwordLayout);
        loginTypeButton.setText(R.string.password_type);
    }

    private void animateView(View show, View hide) {
        long duration = 300;
        AnimUtils.just(hide)
                .setScaleX(1,0)
                .setScaleY(1,0)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .onEnd(animator -> {
                    hide.setVisibility(View.GONE);
                    show.setVisibility(View.VISIBLE);
                    AnimUtils.just(show)
                            .setScaleX(0,1)
                            .setScaleY(0,1)
                            .setDuration(duration)
                            .setInterpolator(new AccelerateInterpolator())
                            .animate();
                }).animate();
    }

    private boolean checkAccount(String text, int length) {
        if (StringUtils.isPhoneNumber(text)) {
            accountLayout.setErrorEnabled(false);
            getVerificationCodeButton.setEnabled(true);
            return true;
        } else {
            if (length == 0) {
                accountLayout.setErrorEnabled(false);
            } else if (length < 11) {
                if (StringUtils.isNumber(text)) {
                    accountLayout.setErrorEnabled(false);
                } else {
                    accountLayout.setErrorEnabled(true);
                    accountLayout.setError("请输入0-9的数字");
                }
            } else if (length == 11) {
                accountLayout.setErrorEnabled(true);
                accountLayout.setError("手机号码无效！");
            } else if (length > 11) {
                accountLayout.setErrorEnabled(true);
                accountLayout.setError("手机号码必须是11位");
            }
            getVerificationCodeButton.setEnabled(false);
            return false;
        }
    }

    @Override
    public String getAccount() {
        if (accountLayout == null) {
            return "";
        }
        return ((TextView) accountLayout.findViewById(R.id.account_text)).getText().toString();
    }

    @Override
    public String getPassword() {
        if (passwordLayout == null) {
            return "";
        }
        return ((TextView) passwordLayout.findViewById(R.id.password_text)).getText().toString();
    }

    @Override
    public boolean isLoginByPassword() {
        return passwordLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void changeLoginType() {
        if (isLoginByPassword()) {
            showVerificationCodeView();
        } else {
            showPasswordView();
        }
    }

    @Override
    public boolean onBackPressed() {
        finishActivity();
        getActivity().overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
        return false;
    }
}
