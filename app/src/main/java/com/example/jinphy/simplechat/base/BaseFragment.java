package com.example.jinphy.simplechat.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseFragment extends Fragment {

    public BaseFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int resourceId = getResourceId();

        View root = inflater.inflate(resourceId, container, false);

        initView(root);

        initData();

        // Must set to true,if you want to use options menu in the fragment
        setHasOptionsMenu(true);

        return root;
    }


    private void initView(View view){

        // 查找所有需要用到的View
        findViewsById(view);

        // 设置Views
        setupViews();

        // 为需要的View注册各种点击事件
        registerEvent();
    }

    abstract protected @LayoutRes int getResourceId();

    protected abstract void initData();

    protected abstract void findViewsById(View view);

    protected abstract void setupViews();

    protected abstract void registerEvent();
}
