package com.example.jinphy.simplechat.modules.main.friends;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.friend.Friend;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface FriendsContract {

    interface View extends BaseView<Presenter> {

        RecyclerView getRecyclerView();

        void updateView();
    }

    interface Presenter extends BasePresenter {

        List<Friend> loadFriends();

    }
}

