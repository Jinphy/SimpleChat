package com.example.jinphy.simplechat.modules.login;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface LoginContract {

    interface View<Presenter> extends BaseView<Presenter>{
    }

    interface Presenter extends BasePresenter{
    }
}
