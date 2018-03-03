package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public interface NewFriendsContract {

    interface View extends BaseView<Presenter> {


    }



    interface Presenter extends BasePresenter{

        List<NewFriendRecyclerViewAdapter.NewFriend> loadNewFriends();

    }
}
