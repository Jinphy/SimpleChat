package com.example.jinphy.simplechat.modules.login;

import android.view.View;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter>{

        void showSignUp(android.view.View view);



    }

    interface Presenter extends BasePresenter{
    }
}
