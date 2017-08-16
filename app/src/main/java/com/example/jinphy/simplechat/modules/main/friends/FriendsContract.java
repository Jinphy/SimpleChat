package com.example.jinphy.simplechat.modules.main.friends;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.Friend;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.main.msg.MsgRecyclerViewAdapter;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface FriendsContract {

    interface View extends BaseView<Presenter> {

        void initFab();

        void fabAction(android.view.View view);

        RecyclerView getRecyclerView();
    }

    interface Presenter extends BasePresenter {

        List<Friend> loadFriends();

        FriendsRecyclerViewAdapter getAdapter();
    }
}

