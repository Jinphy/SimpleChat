package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_view.CustomVerticalStepperFormLayout;
import com.example.jinphy.simplechat.listener_adapters.TextWatcherAdapter;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.json.JSONObject;

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
                .setConfirmStepTitle(getString(R.string.commit_data));


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

    private String getText(View view) {
        TextInputEditText editText = view.findViewById(R.id.input_text);
        return editText.getText().toString();
    }

    private void setText(View view,String text) {
        TextInputEditText editText = view.findViewById(R.id.input_text);
        editText.setText(text);
    }

    private String getText(View view,int stepNumber){
        TextInputEditText editText = view.findViewById(R.id.input_text);
        if (stepNumber == STEP_ACCOUNT || stepNumber == STEP_VERIFICATION_CODE) {
            ViewUtils.showExpressText(editText);
        } else {
            ViewUtils.showCipherText(editText);
        }
        return editText.getText().toString();
    }

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
        nextStepButton.setOnClickListener(v->{
            if (hasVerified) {
                verticalStepperForm.next(STEP_VERIFICATION_CODE);
            } else {
                TextInputEditText editText = verificationCodeView.findViewById(R.id.input_text);
                String verificationCode = editText.getText().toString();
                presenter.submitVerificationCode(verifiedPhone,verificationCode);
            }

        });

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
        Log.d(TAG, "checkVerificationCode: "+text);
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
        presenter.getVerificationCode(phone);

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
            Log.d(TAG, "afterEvent: 错误代码：" + status);
            Log.d(TAG, "afterEvent: 错误描述：" + description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showResultOfGetVerificationCode(String message) {
        Log.d(TAG, "showResultOfGetVerificationCode: "+message);
        Flowable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    Toast.makeText(BaseApplication.INSTANCE, message, Toast.LENGTH_LONG).show();
                })
                .subscribe();
    }

    @Override
    public synchronized void setText(TextView view, String text) {

        view.setText(text);
    }

    @Override
    public void changeViewAfterSubmittingVerificationSuccessfully(String phone) {
        hasVerified = true;
//        verticalStepperForm.next(STEP_VERIFICATION_CODE);
        verticalStepperForm.goToNextStep();
    }

    @Override
    public void changeViewAfterGettingVerificationSuccessfully() {
        TextInputEditText editText = accountView.findViewById(R.id.input_text);
        verifiedPhone = editText.getText().toString();
        Log.d(TAG, "changeViewAfterGettingVerificationSuccessfully: verifiedPhone = "+verifiedPhone);
        hasVerified = false;
        hasGetVerificationCode = true;

        String text = BaseApplication.INSTANCE.getString(R.string.re_get);

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
                    setText( ((TextView) view),
                            BaseApplication.INSTANCE.getString(R.string.get_verification_code));
                })
                .subscribe();

    }


    @Override
    public void changeViewAfterSubmittingVerificationUnSuccessfully() {
        hasVerified = false;
    }

    @Override
    public void changeViewAfterGettingVerificationUnsuccessfully() {
        verifiedPhone = null;
        hasGetVerificationCode = false;
        hasVerified = false;
        verificationCodeView.findViewById(R.id.verification_code_button).setEnabled(true);
    }
}
