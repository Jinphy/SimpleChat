package com.example.jinphy.simplechat.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.modules.welcome.WelcomePresenter;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    public BaseFragment(){}

    protected T presenter;

    protected Callback callback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int resourceId = getResourceId();

        View root = inflater.inflate(resourceId, container, false);

        this.presenter = getPresenter();
        Log.e("Main", "this.presenter ="+this.presenter );

        initView(root);

        initData();

        // Must set to true,if you want to use options menu in the fragment
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.presenter == null) {
            this.presenter = getPresenter();
        }
        this.presenter.start();
    }

    private void initView(View view){

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

    protected T getPresenter(){

        if (callback == null) {
            throw new NullPointerException(
                    "the callback cannot be null,you must invoke the fragment.setCallback() method");
        }
        return (T) callback.getPresenter(this);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback <T extends BasePresenter> {
        T getPresenter(Fragment fragment);
    }
}

