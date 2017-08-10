package com.example.jinphy.simplechat.base;

import android.view.View;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    void initView(View view);

    void initData();


}
