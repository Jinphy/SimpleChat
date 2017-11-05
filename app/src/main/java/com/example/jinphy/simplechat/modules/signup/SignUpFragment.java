package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.mob.MobSDK;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment<SignUpPresenter> implements
        SignUpContract.View, VerticalStepperForm {

    private static final int STEP_ACCOUNT = 0;
    private static final int STEP_VERIFICATION_CODE = 1;
    public static final int STEP_PASSWORD = 2;
    private static final int STEP_CONFIRM_PASSWORD = 3;

    private TextInputLayout accountText;
    private TextInputLayout verificationCodeText;
    private TextInputLayout passwordText;
    private TextInputLayout confirmPasswordText;

    private VerticalStepperFormLayout verticalStepperForm;
    private int colorPrimary;
    private int colorPrimaryDark;
    private String[] titles = {"账号", "验证码", "密码","确认密码"};
    private TextWatcherAdapter editTextWatcher;

    private EventHandler eventHandler;

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
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        verticalStepperForm = view.findViewById(R.id.vertical_steps_form);
        accountText = createStepView(R.string.hint_account);
        verificationCodeText = createStepView(R.string.hint_verification_code);
        passwordText = createStepView(R.string.hint_password);
        confirmPasswordText = createStepView(R.string.hint_confirm_password);
    }

    @Override
    protected void setupViews() {
        colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, titles, this,
                getActivity())
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .hideKeyboard(true)
                .materialDesignInDisabledSteps(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .init();


    }

    @Override
    protected void registerEvent() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerSMSSDK();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
    //-----------------------------------------------

    @Override
    public View createStepContentView(int stepNumber) {
        switch (stepNumber) {
            case STEP_ACCOUNT:
                return accountText;
            case STEP_VERIFICATION_CODE:
                return verificationCodeText;
            case STEP_PASSWORD:
                return passwordText;
            case STEP_CONFIRM_PASSWORD:
                return confirmPasswordText;
            default:
                return null;
        }
    }


    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case STEP_ACCOUNT:
                String account = accountText.getEditText().getText().toString();
                checkAccount(account, account.length());
                break;
            case STEP_VERIFICATION_CODE:
                String verificationCode = verificationCodeText.getEditText().getText().toString();
                checkVerificationCode(verificationCode, verificationCode.length());
                break;
            case STEP_PASSWORD:
                String password = passwordText.getEditText().getText().toString();
                checkPassword(password,password.length());
                break;
            case STEP_CONFIRM_PASSWORD:
                String confirmPassword = accountText.getEditText().getText().toString();
                checkConfirmPassword(confirmPassword, confirmPassword.length());
                break;
            default:
                break;
        }
    }

    @Override
    public void sendData() {

    }

    private TextInputLayout createStepView(@StringRes int hintId) {
        String hint = getString(hintId);
        TextInputLayout inflate = (TextInputLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.text_input_layout, null, false);
        inflate.setHint(hint);

        TextInputEditText editText = inflate.findViewById(R.id.input_text);
        editText.setTag(hintId);
        editText.addTextChangedListener(createTextWatcher(hintId));
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
        if (StringUtils.isPhoneNumber(text)) {
            verticalStepperForm.setActiveStepAsCompleted();
            accountText.setErrorEnabled(false);
        } else {
            verticalStepperForm.setStepAsUncompleted(STEP_ACCOUNT, null);
            if (length == 0) {
                accountText.setErrorEnabled(false);
            } else if (length < 11) {
                if (StringUtils.isNumber(text)) {
                    accountText.setErrorEnabled(false);
                } else {
                    accountText.setErrorEnabled(true);
                    accountText.setError("手机号码必须是数字0-9");
                }
            } else if (length == 11) {
                accountText.setErrorEnabled(true);
                accountText.setError("请输入正确的手机号码！");
            } else if (length > 11) {
                accountText.setErrorEnabled(true);
                accountText.setError("手机号码必须是11位数字");
            }
        }
    }

    private void checkVerificationCode(String text, int length) {





    }

    private void checkPassword(String text, int length) {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void checkConfirmPassword(String text, int length) {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    protected void registerSMSSDK() {
        String appKey = getString(R.string.app_key);
        String appSecret = getString(R.string.app_secret);
        MobSDK.init(getContext(), appKey, appSecret);
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 处理你自己的逻辑
                    }
                }

                try {
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");//错误描述
                    int status = object.optInt("status");//错误代码
                    if (status > 0 && !TextUtils.isEmpty(des)) {
                        Toast.makeText(getContext(), des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    //do something
                }
            }

        };
        SMSSDK.registerEventHandler(eventHandler);

    }


}
