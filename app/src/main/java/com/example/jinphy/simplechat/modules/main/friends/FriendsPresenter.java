package com.example.jinphy.simplechat.modules.main.friends;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsPresenter implements FriendsContract.Presenter {

    FriendsContract.View view;
    private FriendRepository friendRepository;
    private UserRepository userRepository;

    public FriendsPresenter(@NonNull FriendsContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        friendRepository = FriendRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void loadFriends(Context context) {
        String owner = userRepository.currentUser().getAccount();
        if (friendRepository.count(owner) > 0) {
            view.updateFriends(friendRepository.loadLocal(owner));
        } else {
            friendRepository.<List<Friend>>newTask()
                    .param(Api.Key.owner, owner)
                    .doOnDataOk(okData -> {
                        friendRepository.save(okData.getData());
                        view.updateFriends(friendRepository.loadLocal(owner));
                    })
                    .submit(task -> friendRepository.loadOnline(context, task));
        }
    }
}
