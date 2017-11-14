package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_view.CustomVerticalStepperFormLayout;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.utils.Encrypt;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import java.util.concurrent.TimeUnit;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment<SignUpPresenter> implements
        SignUpContract.View, VerticalStepperForm {
    private static final String TAG = "SignUpFragment";
    private static final int STEP_ACCOUNT = 0;
    private static final int STEP_VERIFICATION_CODE = 1;
    public static final int STEP_PASSWORD = 2;
    private static final int STEP_CONFIRM_PASSWORD = 3;

    private static final String KEY_ACCOUNT_IN_VIEW = "KEY_ACCOUNT_IN_VIEW";
    private static final String KEY_VERIFICATION_CODE_IN_VIEW = "KEY_VERIFICATION_CODE_IN_VIEW";
    private static final String KEY_PASSWORD_IN_VIEW = "KEY_PASSWORD_IN_VIEW";
    private static final String KEY_CONFIRM_PASSWORD_IN_VIEW = "KEY_CONFIRM_PASSWORD_IN_VIEW";

    private static final String KEY_HAS_GET_VERIFICATION_CODE = "KEY_HAS_GET_VERIFICATION_CODE";
    private static final String KEY_HAS_VERIFIED = "KEY_HAS_VERIFIED";
    private static final String KEY_VERIFIED_PHONE = "KEY_VERIFIED_PHONE";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";

    private View accountView;
    private View verificationCodeView;
    private View passwordView;
    private View confirmPasswordView;

    private CustomVerticalStepperFormLayout verticalStepperForm;
    private int colorPrimary;
    private int colorPrimaryDark;
    private String[] titles;

    private boolean hasGetVerificationCode;
    private boolean hasVerified;
    private String verifiedPhone;
    private String password;


    interface M{
        int x=0;
    }

    public SignUpFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void initData() {
        titles = new String[]{
                getString(R.string.account),
                getString(R.string.verification_code),
                getString(R.string.password),
                getString(R.string.confirm_password)
        };

    }

    @Override
    protected void findViewsById(View view) {
        verticalStepperForm = view.findViewById(R.id.vertical_steps_form);
        accountView = createStepView(R.string.hint_account);
        verificationCodeView = createStepView(R.string.hint_verification_code);
        passwordView = createStepView(R.string.hint_password);
        confirmPasswordView = createStepView(R.string.hint_confirm_password);
    }

    @Override
    protected void setupViews() {
        colorPrimary = ContextCompat.getColor(getContext(), R.color.color_red_D50000);
        colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.color_red_light_B71C1C);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, titles, this,
                getActivity())
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .hideKeyboard(true)
                .materialDesignInDisabledSteps(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .stepTitleTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent))
                .init();
        verticalStepperForm.hideScrollBar()
                .setNextStepButtonTexts(R.string.next_step)
                .setButtonTextOfConfirmStep(R.string.confirm)
                .setConfirmStepTitle(getString(R.string.commit_data))
                .setNextBottomButtonEnable(false);


    }

    @Override
    protected void registerEvent() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }
        this.presenter.registerSMSSDK(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null) {
            setText(accountView,savedInstanceState.getString(KEY_ACCOUNT_IN_VIEW,""));
            setText(verificationCodeView,savedInstanceState
                    .getString(KEY_VERIFICATION_CODE_IN_VIEW,""));
            setText(passwordView,savedInstanceState.getString(KEY_PASSWORD_IN_VIEW,""));
            setText(confirmPasswordView,savedInstanceState
                    .getString(KEY_CONFIRM_PASSWORD_IN_VIEW,""));

            hasGetVerificationCode = savedInstanceState
                    .getBoolean(KEY_HAS_GET_VERIFICATION_CODE,false);
            hasVerified = savedInstanceState.getBoolean(KEY_HAS_VERIFIED,false);
            verifiedPhone = savedInstanceState.getString(KEY_VERIFIED_PHONE,"");
            password = savedInstanceState.getString(KEY_PASSWORD,"");
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ACCOUNT_IN_VIEW,getText(accountView));
        outState.putString(KEY_VERIFICATION_CODE_IN_VIEW,getText(verificationCodeView));
        outState.putString(KEY_PASSWORD_IN_VIEW,getText(passwordView));
        outState.putString(KEY_CONFIRM_PASSWORD_IN_VIEW,getText(confirmPasswordView));

        outState.putBoolean(KEY_HAS_GET_VERIFICATION_CODE, hasGetVerificationCode);
        outState.putBoolean(KEY_HAS_VERIFIED,hasVerified);
        outState.putString(KEY_VERIFIED_PHONE,verifiedPhone);
        outState.putString(KEY_PASSWORD, password);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.unregisterSMSSDK();
    }
    //-----------------------------------------------

    @Override
    public View createStepContentView(int stepNumber) {
        switch (stepNumber) {
            case STEP_ACCOUNT:
                return accountView;
            case STEP_VERIFICATION_CODE:
                return verificationCodeView;
            case STEP_PASSWORD:
                return passwordView;
            case STEP_CONFIRM_PASSWORD:
                return confirmPasswordView;
            default:
                return null;
        }
    }


    @Override
    public void onStepOpening(int stepNumber) {
        setNextStepButtonListener(stepNumber);
        switch (stepNumber) {
            case STEP_ACCOUNT:
                String account = getText(accountView,stepNumber);
                checkAccount(account, account.length());
                break;
            case STEP_VERIFICATION_CODE:
                doOnVerificationCodeStepOpen();
                String verificationCode = getText(verificationCodeView,stepNumber);
                checkVerificationCode(verificationCode, verificationCode.length());
                break;
            case STEP_PASSWORD:
                String password = getText(passwordView,stepNumber);
                checkPassword(password, password.length());
                break;
            case STEP_CONFIRM_PASSWORD:
                String confirmPassword = getText(confirmPasswordView,stepNumber);
                checkConfirmPassword(confirmPassword, confirmPassword.length());
                break;
            default:
                break;
        }
    }

    /*
    * 获取对应步骤的输入框的文本
    * */
    private String getText(View view) {
        TextInputEditText editText = view.findViewById(R.id.input_text);
        return editText.getText().toString();
    }


    /*
    * 设置对应步骤的输入框的文本
    * */
    private void setText(View view,String text) {
        TextInputEditText editText = view.findViewById(R.id.input_text);
        editText.setText(text);
    }

    /*
    * 获取对应步骤的输入框的文本，并设置是否明文显示，在对应步骤打开时调用
    * */
    private String getText(View view,int stepNumber){
        TextInputEditText editText = view.findViewById(R.id.input_text);
        if (stepNumber == STEP_ACCOUNT || stepNumber == STEP_VERIFICATION_CODE) {
            ViewUtils.showExpressText(editText);
        } else {
            ViewUtils.showCipherText(editText);
        }
        return editText.getText().toString();
    }

    /*
    * 设置 “下一步” 按钮的点击事件
    *
    * */
    private void setNextStepButtonListener(int stepNumber) {
        // 判断是否有下一步
        if (stepNumber < verticalStepperForm.getSteps()) {
            // 有下一步，则设置下一步骤的头部的点击事件
            verticalStepperForm.getStepHeader(stepNumber+1).setOnClickListener(v->{
                if (verticalStepperForm.isStepCompleted(stepNumber + 1)) {
                    verticalStepperForm.next(stepNumber);
                    verticalStepperForm.setNextBottomButtonEnable(true);
                } else {
                    verticalStepperForm.setNextBottomButtonEnable(false);
                }
            });
        }

        AppCompatButton nextStepButton = verticalStepperForm.getNextStepButton(stepNumber);
        switch (stepNumber) {
            case STEP_ACCOUNT:
                nextStepButton.setOnClickListener(v -> {
                    // 如果此时该账号已经获取过验证码，说明该账号是可以注册的，则直接进行下一步
                    if (hasGetVerificationCode) {
                        verticalStepperForm.next(STEP_ACCOUNT);
                        return;
                    }
                    // 当前输入账号还未获取过验证码，需要检测账号是否合法，合法则进行下一步
                    // 返回 yes 说明该账号已经注册过，所以不合法
                    presenter.findUser(getText(accountView), response -> {
                        String yes = "该账号已被注册过，可直接登录！";
                        String error = "服务器异常，请稍后再试！";
                        handleResponse(response,yes,null,error,false);
                        updateViewAfterFindUser(response);
                    });

                });
                break;
            case STEP_VERIFICATION_CODE:
                nextStepButton.setOnClickListener(v->{
                    // 判断当前账号是否验证过
                    if (hasVerified) {
                        // 验证过，则可以进行下一步
                        verticalStepperForm.next(STEP_VERIFICATION_CODE);
                    } else {
                        // 没有验证过，则进行验证，如果成功则下一步，否则显示验证不成功
                        TextInputEditText editText = verificationCodeView.findViewById(R.id.input_text);
                        String verificationCode = editText.getText().toString();
                        presenter.submitVerificationCode(verifiedPhone,verificationCode,response -> {
                            String yes = "已验证，请继续！";
                            String no = "验证失败，请输入正确的验证码！";
                            handleResponse(response,yes,no,null,true);
                            updateViewAfterSubmittingVerificationCode(response);
                        });
                    }

                });
                break;
            case STEP_PASSWORD:
                break;
            case STEP_CONFIRM_PASSWORD:
                break;
            default:
                nextStepButton.setOnClickListener(v -> {
                    BaseApplication.showToast("正在注册......", true );
                    long date = System.currentTimeMillis();
                    presenter.createNewUser(
                            verifiedPhone,
                            Encrypt.md5(password),
                            String.valueOf(date),
                            response -> {
                        String yes = "恭喜您，账号注册成功！";
                        String no = "服务器异常，请稍后再试！";
                        String error = "网络异常，请检查网络是否正常连接！";
                        handleResponse(response, yes, no, error, false);
                        updateViewAfterCreateNewUser(response,date);
                    });
                });
                break;
        }
    }

    private void handleResponse(Response response,String yes,String no,String error,boolean isLong) {
        Flowable.just(response)
                .map(value->value.message)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(message -> {
                    if (Response.yes.equals(message)) {
                        if (yes != null) {
                            BaseApplication.showToast(yes, isLong );
                        }
                    } else if (Response.no.equals(message)) {
                        if (no != null) {
                            BaseApplication.showToast(no,isLong);
                        }
                    }else if(Response.error.equals(message)){
                        if (error != null) {
                            BaseApplication.showToast(error,isLong );
                        }
                    } else {
                        BaseApplication.showToast("网络连接错误，请检查网络是否连接！", isLong);
                    }
                })
                .subscribe();

    }

    /*
    * 当验证码步骤打开时调用，根据不同状态设置不同视图
    * */
    private void doOnVerificationCodeStepOpen(){
        AppCompatButton nextStepButton = verticalStepperForm.
                getNextStepButton(STEP_VERIFICATION_CODE);

        String currentPhone = ((TextInputEditText) accountView
                .findViewById(R.id.input_text)).getText().toString();

        if (StringUtils.notEqual(currentPhone, verifiedPhone)) {
            hasVerified = false;
        }
        if (hasVerified) {
            // 已经通过验证
            verticalStepperForm.setActiveStepAsCompleted();
            nextStepButton.setText(R.string.next_step);
            verificationCodeView.findViewById(R.id.input_layout).setVisibility(View.GONE);
            verificationCodeView.findViewById(R.id.verify_successfully_view).setVisibility(View.VISIBLE);
        }else {
            // 没有通过验证
            verificationCodeView.findViewById(R.id.input_layout).setVisibility(View.VISIBLE);
            verificationCodeView.findViewById(R.id.verify_successfully_view).setVisibility(View.GONE);
            nextStepButton.setText(R.string.confirm);
            TextInputEditText editText = verificationCodeView.findViewById(R.id.input_text);
            String verificationCode = editText.getText().toString();
            if (hasGetVerificationCode && verificationCode.length()>0) {
                verticalStepperForm.setActiveStepAsCompleted();
            } else {
                verticalStepperForm.setStepAsUncompleted(STEP_VERIFICATION_CODE,null);
            }
        }

    }

    @Override
    public void sendData() {

    }

    private View createStepView(@StringRes int hintId) {
        String hint = getString(hintId);
        LinearLayout inflate = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.text_input_layout, null, false);
        TextInputLayout inputLayout = inflate.findViewById(R.id.text_input_layout);
        TextInputEditText editText = inflate.findViewById(R.id.input_text);
        TextView verificationCodeButton = inflate.findViewById(R.id.verification_code_button);

        inputLayout.setHint(hint);
        editText.addTextChangedListener(createTextWatcher(hintId));

        if (hintId == R.string.hint_verification_code) {
            verificationCodeButton.setVisibility(View.VISIBLE);
            verificationCodeButton.setOnClickListener(this::getVerificationCode);
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) inputLayout
                    .getLayoutParams();
            layoutParams.width = ScreenUtils.dp2px(getContext(), 200);
            inputLayout.setLayoutParams(layoutParams);
        }
        return inflate;
    }

    private TextWatcherAdapter createTextWatcher(@StringRes int hintId) {
        return new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                int length = editable.length();
                switch (hintId) {
                    case R.string.hint_account:
                        checkAccount(text, length);
                        break;
                    case R.string.hint_verification_code:
                        checkVerificationCode(text, length);
                        break;
                    case R.string.hint_password:
                        checkPassword(text, length);
                        break;
                    case R.string.hint_confirm_password:
                        checkConfirmPassword(text, length);
                        break;
                    default:
                        break;
                }
            }
        };

    }

    private void checkAccount(String text, int length) {
        TextInputLayout textInputLayout = accountView.findViewById(R.id.text_input_layout);
        if (StringUtils.isPhoneNumber(text)) {
            verticalStepperForm.setActiveStepAsCompleted();
            textInputLayout.setErrorEnabled(false);
        } else {
            verticalStepperForm.setStepAsUncompleted(STEP_ACCOUNT, null);
            if (length == 0) {
                textInputLayout.setErrorEnabled(false);
            } else if (length < 11) {
                if (StringUtils.isNumber(text)) {
                    textInputLayout.setErrorEnabled(false);
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError("请输入0-9的数字");
                }
            } else if (length == 11) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("手机号码无效！");
            } else if (length > 11) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("手机号码必须是11位");
            }
        }
    }

    private void checkVerificationCode(String text, int length) {
        if (hasVerified) {
            return;
        }
        if (hasGetVerificationCode && length > 0) {
            verticalStepperForm.setActiveStepAsCompleted();
        }else {
            verticalStepperForm.setStepAsUncompleted(STEP_VERIFICATION_CODE, null);
        }
    }

    private void checkPassword(String text, int length) {
        TextInputLayout inputLayout = passwordView.findViewById(R.id.text_input_layout);
        if (StringUtils.isNumberOrLetter(text)) {
            password = text;
            verticalStepperForm.setActiveStepAsCompleted();
            inputLayout.setErrorEnabled(false);
        }else {
            password = null;
            verticalStepperForm.setStepAsUncompleted(STEP_PASSWORD, null);
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("密码必须是6-20数字或字母！");
        }
    }

    private void checkConfirmPassword(String text, int length) {
        if (StringUtils.equal(text, password)) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            verticalStepperForm.setStepAsUncompleted(STEP_CONFIRM_PASSWORD,null);
        }
    }

    // 获取验证码按钮的点击事件
    private void getVerificationCode(View view) {
        view.setEnabled(false);
        TextInputEditText editText = accountView.findViewById(R.id.input_text);
        String phone = editText.getText().toString();
        presenter.getVerificationCode(phone,response -> {
            String yes = "验证码已发送，请正确输入！";
            String no = "获取验证码失败！";
            handleResponse(response, yes, no, null, true);
            updateViewAfterGettingVerificationCode(response);
        });

    }

    @Override
    public synchronized void setText(TextView view, String text) {

        view.setText(text);
    }

    @Override
    public void updateViewAfterSubmittingVerificationCode(Response response) {
        Flowable.just(response)
                .map(value -> value.message)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(message -> {
                    if (Response.yes.equals(message)) {
                        hasVerified = true;
                        //        verticalStepperForm.next(STEP_VERIFICATION_CODE);
                        verticalStepperForm.goToNextStep();
                    } else {
                        hasVerified = false;
                    }
                })
                .subscribe();
    }

    @Override
    public void updateViewAfterGettingVerificationCode(Response response) {
        Flowable.just(response)
                .map(value -> value.message)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(message -> {
                    if (Response.yes.equals(message)) {
                        TextInputEditText editText = accountView.findViewById(R.id.input_text);
                        verifiedPhone = editText.getText().toString();
                        hasVerified = false;
                        hasGetVerificationCode = true;

                        String text = BaseApplication.instance().getString(R.string.re_get);

                        View view = verificationCodeView.findViewById(R.id.verification_code_button);

                        Flowable.intervalRange(1, 60, 0, 1, TimeUnit.SECONDS)
                                .map(value -> {
                                    value = 60 - value;
                                    return text.replace(" ", value + "");
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(value -> setText((TextView) view, value))
                                .doOnComplete(() -> {
                                    view.setEnabled(true);
                                    setText(((TextView) view),
                                            BaseApplication.instance().getString(R.string.get_verification_code));
                                })
                                .subscribe();

                    } else {
                        verifiedPhone = null;
                        hasGetVerificationCode = false;
                        hasVerified = false;
                        verificationCodeView.findViewById(R.id.verification_code_button).setEnabled(true);
                    }
                })
                .subscribe();

    }

    @Override
    public void updateViewAfterCreateNewUser(Response response,long date) {
        Flowable.just(response)
                .map(value -> value.message)
                .map(message -> {
                    if (Response.yes.equals(message)) {
                        User user = presenter.saveUser(
                                verifiedPhone,
                                Encrypt.md5(password),
                                date);
                        return user;
                    }
                    return Response.no;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(user -> {
                    if (user instanceof User) {
                        new MaterialDialog.Builder(getContext())
                                .title("注册成功")
                                .iconRes(R.drawable.ic_done)
                                .content("恭喜你成功注册账号，是否立即登录？")
                                .positiveText("马上登录")
                                .negativeText("不了不了")
                                .titleColorRes(R.color.color_red_D50000)
                                .positiveColorRes(R.color.half_alpha_gray)
                                .onPositive((dialog, which) -> {
                                    // TODO: 2017/11/7 执行登录操作
                                })
                                .onNeutral((dialog, which) -> {
                                    getActivity().finish();
                                })
                                .show();
                    }
                })
                .subscribe();


    }

    @Override
    public void updateViewAfterFindUser(Response response) {
        Flowable.just(response)
                .map(value -> value.message)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(message -> {
                    if (Response.yes.equals(message)) {
                        // do nothing
                    } else if (Response.no.equals(message)) {
                        verticalStepperForm.next(STEP_ACCOUNT);
                    } else {
                        // do nothing
                    }
                })
                .subscribe();
    }
}
