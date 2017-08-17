package com.example.jinphy.simplechat.modules.main.self;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.modules.main.MainFragment;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface SelfContract {

    interface View extends BaseView<Presenter> {

        void initFab();

        void fabAction(android.view.View view);

        boolean handleTouchEvent(MotionEvent event);

        void handleOnViewPagerScrolled(int position, float offset, int offsetPixels);
    }

    interface Presenter extends BasePresenter {
    }
}

