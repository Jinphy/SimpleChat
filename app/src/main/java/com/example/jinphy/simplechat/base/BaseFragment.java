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

    private static final String TAG = "BaseFragment";

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

    //========================================================\\

}

