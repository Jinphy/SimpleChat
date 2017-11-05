package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.mob.MobSDK;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

    private View accountView;
    private View verificationCodeView;
    private View passwordView;
    private View confirmPasswordView;

    private VerticalStepperFormLayout verticalStepperForm;
    private int colorPrimary;
    private int colorPrimaryDark;
    private String[] titles;

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
        titles  = new String[] {
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
        this.presenter.registerSMSSDK(getContext());
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
        switch (stepNumber) {
            case STEP_ACCOUNT:
                String account = ((TextInputEditText) accountView
                        .findViewById(R.id.input_text)).getText().toString();
                checkAccount(account, account.length());
                break;
            case STEP_VERIFICATION_CODE:
                String verificationCode = ((TextInputEditText) verificationCodeView
                        .findViewById(R.id.input_text)).getText().toString();
                checkVerificationCode(verificationCode, verificationCode.length());
                break;
            case STEP_PASSWORD:
                String password = ((TextInputEditText) passwordView
                        .findViewById(R.id.input_text)).getText().toString();
                checkPassword(password,password.length());
                break;
            case STEP_CONFIRM_PASSWORD:
                String confirmPassword = ((TextInputEditText) accountView
                        .findViewById(R.id.input_text)).getText().toString();
                checkConfirmPassword(confirmPassword, confirmPassword.length());
                break;
            default:
                break;
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
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) inputLayout.getLayoutParams();
            layoutParams.width = ScreenUtils.dp2px(getContext(),200);
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





    }

    private void checkPassword(String text, int length) {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void checkConfirmPassword(String text, int length) {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    // 获取验证码按钮的点击事件
    private void getVerificationCode(View view) {
        view.setEnabled(false);
        TextInputEditText editText = accountView.findViewById(R.id.input_text);
        String phone = editText.getText().toString();
        presenter.getVerificationCode(phone);
        Flowable.intervalRange(1, 60, 1, 1, TimeUnit.SECONDS)
                .map(value -> {
                    value = 60 - value;
                    String out = getString(R.string.re_get);
                    return out.replace(" ", value + "");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(((TextView)view)::setText)
                .doOnComplete(()->{
                    view.setEnabled(true);
                    ((TextView) view).setText(R.string.get_verification_code);
                })
                .subscribe();
    }

    @Override
    public void logErrorMessageOfSMSSDK(Object data) {
        try {
            Throwable throwable = (Throwable) data;
            throwable.printStackTrace();
            JSONObject object = new JSONObject(throwable.getMessage());
            String description = object.optString("detail");//错误描述
            int status = object.optInt("status");//错误代码
            Log.d(TAG, "afterEvent: -------------请求失败--------------------");
            Log.d(TAG, "afterEvent: 错误代码："+status);
            Log.d(TAG, "afterEvent: 错误描述："+description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showResultOfGetVerificationCode(String message) {
        Flowable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    return null;
                })
                .subscribe();

    }
}
