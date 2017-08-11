package com.example.jinphy.simplechat.modules.main.routine;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.modules.main.MainFragment;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface RoutineContract {

    interface View extends BaseView<Presenter> {

        void setMainFragment(@NonNull MainFragment mainFragment);

        void initFab();

        void fabAction(android.view.View view);
    }

    interface Presenter extends BasePresenter {
    }
}

