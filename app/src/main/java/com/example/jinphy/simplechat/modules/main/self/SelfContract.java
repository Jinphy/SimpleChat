package com.example.jinphy.simplechat.modules.main.self;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.menu.Self;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface SelfContract {

    interface View extends BaseView<Presenter> {

        void initFab();

        void fabAction(android.view.View view);

        void handleOnViewPagerScrolled(int position, float offset, int offsetPixels);

        void moveVertical(float factor);

        void animateVertical(float fromFactor, float toFactor);

        boolean canMoveUp();

        boolean canMoveDown();
    }

    interface Presenter extends BasePresenter {

        SelfRecyclerViewAdapter getAdapter();

        int getItemCount();
    }
}

