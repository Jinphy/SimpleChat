package com.example.jinphy.simplechat.modules.group.create_group;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;

import java.util.List;
import java.util.Map;

/**
 * Created by Jinphy on 2018/3/6.
 */

public interface CreateGroupContract {


    interface View extends BaseView<Presenter> {
        void pickPhoto();

        void setAutoAdd();

        void whenCreateGroupOk(Group group);

    }



    interface Presenter extends BasePresenter{

        String getAccessToken();

        String getCurrentAccount();

        void createGroup(Context context, Map<String, Object> params);

        List<CheckedFriend> loadFriends();

        void saveMembers(Group group, List<String> members);

    }
}
