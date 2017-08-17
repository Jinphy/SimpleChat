package com.example.jinphy.simplechat.modules.active_zoom;

import android.view.MotionEvent;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * Created by jinphy on 2017/8/15.
 */

public interface ActiveZoneContract {


    interface View extends BaseView<Presenter> {

        void moveVertical(float factor);

        void moveHorizontal(float factor);

        void animateHorizontal(float fromFactor, float toFactor,boolean exit);

        void onBackPressed();

    }



    interface Presenter extends BasePresenter{

        ActiveZoneRecyclerViewAdapter getAdapter();

        int getItemCount();

        void onBackPressed();

    }
}

