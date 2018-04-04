package com.example.jinphy.simplechat.modules.main.self;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.user.User;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface SelfContract {

    interface View extends BaseView<Presenter> {

        void handleOnViewPagerScrolled(int position, float offset, int offsetPixels);

        void setupUser();

        void whenLogout();
    }

    interface Presenter extends BasePresenter {

        User getUser();

        void logout(Context context, String account, String accessToken);

        void setNeedMoveUp(boolean moveUp);

        boolean needMoveUp();
    }
}

