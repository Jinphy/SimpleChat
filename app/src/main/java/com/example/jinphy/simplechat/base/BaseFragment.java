package com.example.jinphy.simplechat.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    public BaseFragment() {
    }

    protected View rootView;

    protected T presenter;

    protected PresenterCallback presenterCallback;

    protected FragmentCallback fragmentCallback;

    private static final String TAG = "BaseFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        this.presenter = getPresenter();

        int resourceId = getResourceId();

        View root = inflater.inflate(resourceId, container, false);

        rootView = root;

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

    private void initView(View view) {

        // 查找所有需要用到的View
        findViewsById(view);

        // 设置Views
        setupViews();

        // 为需要的View注册各种点击事件
        registerEvent();
    }

    protected abstract
    @LayoutRes
    int getResourceId();

    protected abstract void initData();

    protected abstract void findViewsById(View view);

    protected abstract void setupViews();

    protected abstract void registerEvent();

    protected T getPresenter() {
        if (presenterCallback == null) {
            throw new NullPointerException(
                    "in " + this.getClass().getSimpleName() +
                            " the presenterCallback cannot be null,you must invoke the fragment" +
                            ".setPresenterCallback() method");
        }
        return (T) presenterCallback.getPresenter(this);
    }

    protected <V> V getFragment() {
        if (fragmentCallback == null) {
            throw new NullPointerException(
                    "in " + this.getClass().getSimpleName() +
                            " the fragmentCallback cannot be null,you must invoke the fragment" +
                            ".setFragmentCallback() method");

        }
        return (V) fragmentCallback.getFragment();
    }

    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean handleVerticalTouchEvent(MotionEvent event) {
        return false;
    }

    //========================================================\\

    public void setPresenterCallback(PresenterCallback callback) {
        this.presenterCallback = callback;
    }

    public <V extends Fragment> void setFragmentCallback(FragmentCallback<V> callback) {
        this.fragmentCallback = callback;
    }


    public interface PresenterCallback<T extends BasePresenter> {
        T getPresenter(Fragment fragment);
    }

    public interface FragmentCallback<T extends Fragment> {
        T getFragment();
    }

}

