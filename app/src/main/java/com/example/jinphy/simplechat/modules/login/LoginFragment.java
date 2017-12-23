package com.example.jinphy.simplechat.modules.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Api.Response;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.model.event_bus.EBFinishActivityMsg;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.modules.main.MainActivity;
import com.example.jinphy.simplechat.modules.signup.SignUpActivity;
import com.example.jinphy.simplechat.modules.welcome.WelcomeActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.Encrypt;
import com.example.jinphy.simplechat.utils.PermissionUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

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
        editText.addTextChangedListener((TextListener) editable -> {
            String text = editable.toString();
            LoginFragment.this.checkAccount(text, text.length());
        });
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


    //==============普通函数=====================================

    /**
     * DESC: 从服务器请求发送验证码
     * Created by Jinphy, on 2017/12/6, at 16:29
     */
    private synchronized void getVerificationCode(){
        String account = getAccount();
        if (StringUtils.equal(verifiedAccount, account)) {
            BaseApplication.showToast("该账号已经验证过，无需再验证！", false);
            return;
        }
        getVerificationCodeButton.setEnabled(false);

        // 在获取验证码之前要先判断当前账号是否存在
        presenter.findUser(getContext(),account,"正在获取验证码，请稍等！","getVerificationCode");
    }

    /**
     * DESC: 登录
     * Created by Jinphy, on 2017/12/6, at 16:29
     */
    private synchronized void doLogin() {
        String account = getText(accountLayout,R.id.account_text);
//        判断账号格式是否合法
        if (!checkAccount(account, account.length())) {
            BaseApplication.showToast("请输入有效的账户！", false);
            return;
        }
//        判断登录方式
        if (isLoginByPassword()) {
            doLoginByPassword();
        } else {
//            验证码方式登录
            doLoginByVerificationCode();
        }
    }

    /**
     * DESC: 通过密码方式登录
     * Created by Jinphy, on 2017/12/6, at 16:28
     */
    private void doLoginByPassword() {
        String account = getAccount();
        String password = getPassword();
        if (password.length() == 0) {
            BaseApplication.showToast("请输入密码！",true);
            return;
        }
        // 登陆前先查询账号是否存在
        presenter.findUser(getContext(), account, "正在登录，请稍等……","doLoginByPassword");

    }

    /**
     * DESC: 通过验证码方式登录
     * Created by Jinphy, on 2017/12/6, at 16:28
     */
    private void doLoginByVerificationCode() {
        String account = getAccount();
        String code = getCode();
        if (StringUtils.equal(account, verifiedAccount)) {
            presenter.submitVerificationCode(getContext(),account,code,"正在登录，请稍等……");
        } else {
            BaseApplication.showToast("请先验证账号！", false);
        }
    }

    /**
     * DESC: 获取文本框中的文本
     * Created by Jinphy, on 2017/12/6, at 16:28
     */
    private String getText(View container, @IdRes int id) {
        if (container == null) {
            return "";
        }
        EditText editText = container.findViewById(id);
        return editText.getText().toString();
    }

    //-----------------EventBus---------------------------------



    //----------mvp中View的函数--------------------------------------

    @Override
    public void findUserOnNext(Response response, final String tag) {
        switch (tag) {
            case "getVerificationCode":// 获取验证码前查询账户是否存在
                if (Response.NO.equals(response.getCode())) {
                    // 查询用户失败
                    BaseApplication.showToast(response.getMsg(), false);
                    getVerificationCodeButton.setEnabled(true);
                    break;
                }
                // 查询用户成功
                presenter.getVerificationCode(getContext(), getAccount());
                break;
            case "doLoginByPassword":// 登录前查询账户是否存在
                if (Response.NO.equals(response.getCode())) {
                    // 查询用户失败
                    BaseApplication.showToast(response.getMsg(), false);
                    break;
                }
                // 查询用户成功
                presenter.login(getContext(), getAccount(), Encrypt.md5(getPassword()), getDeviceId());
            default:
                break;

        }
    }

    @Override
    public void loginOnNext(Response response,String account,String password) {
        BaseApplication.showToast(response.getMsg(), false);
        if (Response.NO.equals(response.getCode())) {
            return;
        }
        // 登录成功
        // 发送给MainActivity
        boolean rememberPassword = rememberMeBox.isChecked();
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        MainActivity.start(getActivity(),user, rememberPassword);
        EventBus.getDefault().post(new EBFinishActivityMsg(WelcomeActivity.class));
        finishActivity();
    }

    @Override
    public void getVerificationCodeOnNext(Response response,String account) {
        BaseApplication.showToast(response.getMsg(), false);
        if (Response.NO.equals(response.getCode())) {
            // 获取验证码失败
            getVerificationCodeButton.setEnabled(true);
            return;
        }
        //获取验证码成功
        verifiedAccount = account;
        // 倒计时获取验证码按钮
        String origin = getVerificationCodeButton.getText().toString();
        String text = BaseApplication.instance().getString(R.string.re_get);
        Flowable.intervalRange(1, 60, 0, 1, TimeUnit.SECONDS)
                .map(value -> text.replace(" ", (60 - value) + ""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(value -> getVerificationCodeButton.setText(value))
                .doOnComplete(() -> {
                    getVerificationCodeButton.setEnabled(true);
                    getVerificationCodeButton.setText(origin);
                })
                .subscribe();
    }

    @Override
    public void submitVerificationCodeOnNext(Response response) {
        if (Response.NO.equals(response.getCode())) {
            BaseApplication.showToast(response.getMsg(), false);
            return;
        }
        presenter.login(getContext(), getAccount(), "null", getDeviceId());
    }

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

    /**
     * DESC: 判断账号是否合法
     * Created by Jinphy, on 2017/12/6, at 16:27
     */
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

    /**
     * DESC: 获取文本框中的账号
     * Created by Jinphy, on 2017/12/6, at 15:48
     */
    @Override
    public String getAccount() {
        return getText(accountLayout, R.id.account_text);
    }

    /**
     * DESC: 获取文本框中的账号密码
     * Created by Jinphy, on 2017/12/6, at 15:48
     */
    @Override
    public String getPassword() {
        return getText(passwordLayout, R.id.password_text);
    }

    /**
     * DESC: 从文本框中获取验证码
     * Created by Jinphy, on 2017/12/6, at 15:42
     */
    @Override
    public String getCode() {
        return getText(verificationCodeLayout, R.id.verification_code_text);
    }

    /**
     * DESC: 获取设备ID
     * Created by Jinphy, on 2017/12/6, at 15:51
     */
    public String getDeviceId() {
        return Encrypt.md5(DeviceUtils.devceId());
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
