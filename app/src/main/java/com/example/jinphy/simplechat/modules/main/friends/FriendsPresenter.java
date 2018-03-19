package com.example.jinphy.simplechat.modules.main.friends;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 *
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
    public List<Friend> loadFriends() {
        String owner = userRepository.currentUser().getAccount();
        return friendRepository.loadLocal(owner);
    }
}
