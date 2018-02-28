package com.example.jinphy.simplechat.modules.add_friend;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.friend.Friend;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/1/15.
 */

public interface AddFriendContract {

    interface View extends BaseView<Presenter> {

        void finish();
    }

    interface Presenter extends BasePresenter{

        void addFriend(Context context, Map<String, Object> params);

        String getCurrentAccount();
    }
}
