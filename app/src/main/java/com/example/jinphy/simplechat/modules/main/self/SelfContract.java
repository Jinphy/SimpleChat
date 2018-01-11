package com.example.jinphy.simplechat.modules.main.self;

import android.app.Activity;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.user.User;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface SelfContract {

    interface View extends BaseView<Presenter> {

        void initFab(Activity activity);

        void fabAction(android.view.View view);

        void handleOnViewPagerScrolled(int position, float offset, int offsetPixels);

        void moveVertical(float factor);

        void animateVertical(float fromFactor, float toFactor);

        boolean canMoveUp();

        boolean canMoveDown();

        void setupUser();
    }

    interface Presenter extends BasePresenter {

        SelfRecyclerViewAdapter getAdapter();

        int getItemCount();

        User getUser();
    }
}

