package com.example.jinphy.simplechat.modules.chat;

import android.view.View;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.Message;

import java.util.List;

/**
 * Created by jinphy on 2017/8/13.
 */

public interface ChatContract {


    interface View extends BaseView<Presenter> {

        void showBar(android.view.View view);

        void hideBar(android.view.View view);

        void animateBar(android.view.View view, float fromValue, float toValue, boolean showBar);

        void fabAction(android.view.View view);
    }

    interface Presenter extends BasePresenter {

        List<Message> loadMessages();

        ChatRecyclerViewAdapter getAdapter();

        int getItemCount();
    }
}

