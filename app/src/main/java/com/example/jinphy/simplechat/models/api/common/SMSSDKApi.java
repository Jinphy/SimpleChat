package com.example.jinphy.simplechat.models.api.common;

import android.content.Context;

import com.example.jinphy.simplechat.custom_view.LoadingDialog;
import com.example.jinphy.simplechat.utils.DialogUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 包可见
 * Created by jinphy on 2017/11/5.
 */

class SMSSDKApi implements ApiInterface<Response<String>> {

    private static final String TAG = "SMSSDKApi";
    private static String countryChina = "86";

    private WeakReference<Context> context;
    private EventHandler eventHandler;
    protected ApiCallback.OnResponseYes<Response<String>> onResponseYes;
    protected ApiCallback.OnResponseNo<Response<String>> onResponseNo;
    protected String phone;
    protected String verificationCode;
    protected String path;
    protected boolean showProgress;
    private LoadingDialog dialog;
    private CharSequence hint = "加载中...";
    private boolean autoShowNo = true;  // 当返回码错误时，是否自动显示对话框，默认显示

    /**
     * DESC: 创建对象
     * Created by Jinphy, on 2017/12/6, at 13:00
     */
    public static ApiInterface<Response<String>> create(Context context) {
        return new SMSSDKApi(context);
    }

    protected SMSSDKApi(Context context) {
        this.context = new WeakReference<>(context);
        init(context);
    }

    protected void init(Context context) {
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int eventCode, int resultCode, Object data) {
                unregister();
                Flowable.just(resultCode)
                        .observeOn(Schedulers.io())
                        .map(code->{
                            Thread.sleep(400);// 延迟0.4秒在返回结果
                            return code;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(SMSSDKApi.this::parseResponse)
                        .subscribe();
            }
        };
        register(context);

    }


    /*
     * DESC: 处理网络请求结果
     * Created by jinphy, on 2017/12/6, at 23:43
     */
    protected void parseResponse(int resultCode) {
        if (showProgress) {
            dialog.dismiss();
        }
        Response<String> response;
        if (resultCode == SMSSDK.RESULT_ERROR) {
            if (Api.Path.getVerificationCode.equals(path)) {
                response = new Response<>(Response.NO, "获取验证码失败！", "");
            } else {
                response = new Response<>(Response.NO, "验证失败，请输入正确的验证码！", "");
            }
            if (autoShowNo&& ObjectHelper.reference(context)) {
                DialogUtils.showResponseNo(response, context.get());
            }
            if (this.onResponseNo != null) {
                this.onResponseNo.call(response);
            }
        } else {
            if (Api.Path.getVerificationCode.equals(path)) {
                response = new Response<>(Response.YES, "验证码已发，请输入！", "");
            } else {
                response = new Response<>(Response.YES, "验证成功，请继续！", "");
            }
            if (this.onResponseYes != null) {
                this.onResponseYes.call(response);
            }
        }
    }

    /**
     * DESC: 设置手机号码
     * Created by jinphy, on 2017/12/6, at 0:03
     */
    protected void phone(String phone) {
        this.phone = phone;
    }

    /**
     * DESC: 设置验证码
     * Created by jinphy, on 2017/12/6, at 0:04
     */
    protected void verificationCode(String code) {
        this.verificationCode = code;
    }

    protected void register(Context context) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    protected void unregister() {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * DESC: 设置请求参数
     * Created by jinphy, on 2017/12/6, at 0:22
     */
    @Override
    public ApiInterface<Response<String>> param(String key, Object value) {
        if (Api.Key.phone.equals(key)) {
            phone(value.toString());
        } else if (Api.Key.verificationCode.equals(key)) {
            verificationCode(value.toString());
        }
        return this;
    }

    @Override
    public  ApiInterface<Response<String>>params(Map<String, Object> params) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            param(param.getKey(), param.getValue());
        }
        return this;
    }



    @Override
    public <U> ApiInterface<Response<String>> api(ApiInterface<U> api) {
        ObjectHelper.throwRuntime("SMSSDKApi cannot invoke this method");
        return null;
    }

    /**
     * DESC: 设置是否显示进度条
     * Created by Jinphy, on 2017/12/6, at 9:21
     */
    @Override
    public ApiInterface<Response<String>> showProgress(boolean... show) {
        if (show.length > 0 && !show[0]) {
            this.showProgress = false;
        } else {
            this.showProgress = true;
            dialog = LoadingDialog.builder(context.get()).title(hint).cancellable(false).build();
        }
        return this;
    }

    /**
     * DESC: 当返回码错误时，是否自动显示对话框，默认显示
     * Created by jinphy, on 2018/1/2, at 20:13
     */
    @Override
    public ApiInterface<Response<String>> autoShowNo(boolean showNo) {
        this.autoShowNo = showNo;
        return this;
    }

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface<Response<String>> connectTimeout(int timeout) {
        // no-op
        return this;
    }


    /**
     * DESC: 设置读取超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface<Response<String>> readTimeout(int timeout) {
        // no-op
        return this;
    }



    @Override
    public ApiInterface<Response<String>> cancellable(boolean... cancel) {
        return this;
    }


    @Override
    public ApiInterface<Response<String>> useCache(boolean... useCache) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> hint(CharSequence hint) {
        this.hint = hint;
        if (this.dialog != null) {
            this.dialog.title(this.hint);
        }
        return this;
    }

    @Override
    public ApiInterface<Response<String>> dataType(Api.Data dataType, Class<?>[] dataClass) {
        return this;
    }

    /**
     * DESC: 设置请求路径
     * Created by jinphy, on 2017/12/6, at 0:05
     */
    @Override
    public ApiInterface<Response<String>> path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onResponse(ApiCallback.OnResponse<Response<String>> onResponse) {
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onResponseYes(ApiCallback.OnResponseYes<Response<String>> onResponseYes) {
        this.onResponseYes = onResponseYes;
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onResponseNo(ApiCallback.OnResponseNo<Response<String>> onResponseNo) {
        this.onResponseNo = onResponseNo;
        return this;
    }

    @Override
    public ApiInterface<Response<String>> baseUrl(String baseUrl) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> port(String port) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> header(String key, String value) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onError(ApiCallback.OnError onError) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onStart(ApiCallback.OnStart onStart) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onFinal(ApiCallback.OnFinal onFinal) {
        return this;
    }

    @Override
    public ApiInterface<Response<String>> onCancel(ApiCallback.OnCancel onCancel) {
        return this;
    }

    @Override
    public ApiInterface<Response<String>> setup(ApiCallback.Setup<ApiInterface<Response<String>>>
                                                            action) {
        if (action != null) {
            action.call(this);
        }
        return this;
    }

    /**
     * DESC: 请求网络
     * Created by jinphy, on 2017/12/6, at 0:06
     */
    public void request() {
        ObjectHelper.requireNonNull(path, "请设置网络请求接口！");
        ObjectHelper.requireNonNull(phone, "请设置手机号码！");
        if (this.showProgress) {
            dialog.show();
        }
        if (Api.Path.getVerificationCode.equals(path)) {
            SMSSDK.getVerificationCode(countryChina, phone);
        } else if (Api.Path.submitVerificationCode.equals(path)) {
            ObjectHelper.requireNonNull(verificationCode, "请设置验证码！");
            SMSSDK.submitVerificationCode(countryChina, phone, verificationCode);
        } else {
            throw new RuntimeException("请设置正确的请求路径！");
        }
    }
}
