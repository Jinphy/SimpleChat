package com.example.jinphy.simplechat.modules.main.routine;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.modules.main.MainFragment;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface RoutineContract {

    interface View extends BaseView<Presenter> {

        void initFab();

        void fabAction(android.view.View view);

        void showActiveZoneActivity();
    }

    interface Presenter extends BasePresenter {

        RoutineRecyclerViewAdapter getAdapter();

        void handleRecyclerViewEvent(android.view.View view, Object item, int type, int position);
    }
}

