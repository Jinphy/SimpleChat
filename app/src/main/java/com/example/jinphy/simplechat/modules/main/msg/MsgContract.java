package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.modules.main.MainFragment;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface MsgContract {


    interface View extends BaseView<Presenter> {

        void setMainFragment(@NonNull MainFragment mainFragment);
    }



    interface Presenter extends BasePresenter {
    }
}

