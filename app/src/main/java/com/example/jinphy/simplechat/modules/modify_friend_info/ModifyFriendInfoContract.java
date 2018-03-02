package com.example.jinphy.simplechat.modules.modify_friend_info;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.friend.Friend;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public interface ModifyFriendInfoContract {

    interface View extends BaseView<Presenter> {

        void afterAddFriend();

        void afterModifyRemark();

        void afterModifyStatus();

        void afterDeleteFriend();
    }



    interface Presenter extends BasePresenter {

        Friend getFriend(String account);

        void addFriend(Context context, Map<String, Object> params);

        void saveFriend(Friend friend);

        void modifyRemark(Context context, Map<String, Object> params);

        void modifyStatus(Context context, Map<String, Object> params);

        void deleteFriend(Context context, Map<String, Object> params);

        void deleteFriendLocal(Friend friend);
    }
}

