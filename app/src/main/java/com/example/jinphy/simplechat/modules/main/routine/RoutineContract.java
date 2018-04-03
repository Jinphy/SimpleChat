package com.example.jinphy.simplechat.modules.main.routine;

import android.app.Activity;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.menu.Routine;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface RoutineContract {

    interface View extends BaseView<Presenter> {

        void initFab(Activity activity);

        void fabAction(android.view.View view);

        void showActiveZoneActivity();

        void showGroupListActivity();

    }

    interface Presenter extends BasePresenter {
    }
}

