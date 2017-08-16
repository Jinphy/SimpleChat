package com.example.jinphy.simplechat.modules.active_zoom;

import android.view.MotionEvent;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/15.
 */

public interface ActiveZoneContract {


    interface View extends BaseView<Presenter> {
        boolean dispatchTouchEvent(MotionEvent event);
    }



    interface Presenter extends BasePresenter{

        ActiveZoneRecyclerViewAdapter getAdapter();

        int getItemCount();

        boolean dispatchTouchEvent(MotionEvent event);
    }
}

