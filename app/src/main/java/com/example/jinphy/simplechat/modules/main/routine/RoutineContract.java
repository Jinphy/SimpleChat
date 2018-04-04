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

    }

    interface Presenter extends BasePresenter {
    }
}

