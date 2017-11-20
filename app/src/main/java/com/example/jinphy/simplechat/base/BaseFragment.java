package com.example.jinphy.simplechat.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jinphy.simplechat.api.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    public BaseFragment() {
    }

    protected View rootView;

    protected T presenter;

    private static final String TAG = "BaseFragment";

    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        this.presenter = getPresenter();

        int resourceId = getResourceId();

        View root = inflater.inflate(resourceId, container, false);

        initData();

        rootView = root;

        initView(root);


        // Must set to true,if you want to use options menu in the fragment
        setHasOptionsMenu(true);


        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.presenter == null) {
            this.presenter = getPresenter();
        }
        this.presenter.start();
        this.toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
    }

    private void initView(View view) {

        // 查找所有需要用到的View
        findViewsById(view);

        // 设置Views
        setupViews();

        // 为需要的View注册各种点击事件
        registerEvent();
    }

    protected abstract @LayoutRes int getResourceId();

    protected abstract void initData();

    protected abstract void findViewsById(View view);

    protected abstract void setupViews();

    protected abstract void registerEvent();

    protected T getPresenter() {
        BaseActivity activity = (BaseActivity) getActivity();
        return activity.getPresenter(this);

        //        if (presenterCallback == null) {
        //            throw new NullPointerException(
        //                    "in " + this.getClass().getSimpleName() +
        //                            " the presenterCallback cannot be null,you must invoke the fragment" +
        //                            ".setPresenterCallback() method");
        //        }
//        return (T) presenterCallback.getPresenter(this);
    }

    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean handleVerticalTouchEvent(MotionEvent event) {
        return false;
    }

    //    处理网络请求的响应
    protected void handleResponse(Response response, String yes, String no, String error, boolean isLong) {
        Flowable.just(response)
                .map(value -> value.message)
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
                }).subscribe();


    }


    protected void finishActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

//    这个方法在activity的onBackPressed中调用
    public boolean onBackPressed(){
//        返回true则会执行activity的onBackPressed()
        return true;
    }

    //========================================================\\

    @Subscribe
    public void eventBus(String message) {

    }

}

