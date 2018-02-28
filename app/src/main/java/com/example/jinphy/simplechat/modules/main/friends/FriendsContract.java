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

        void initFab(Activity activity);

        void fabAction(android.view.View view);

        RecyclerView getRecyclerView();

        void updateFriends(List<Friend> friends);
    }

    interface Presenter extends BasePresenter {

        void loadFriends(Context context);


    }
}

