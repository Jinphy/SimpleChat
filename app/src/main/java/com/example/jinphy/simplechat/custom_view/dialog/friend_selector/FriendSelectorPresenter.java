package com.example.jinphy.simplechat.custom_view.dialog.friend_selector;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public class FriendSelectorPresenter {

    WeakReference<FriendSelector> view;
    FriendRepository friendRepository;
    UserRepository userRepository;


    public FriendSelectorPresenter(FriendSelector view) {
        this.view = new WeakReference<>(view);
        this.friendRepository = FriendRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
    }


    public List<CheckedFriend> loadFriends(List<String> exclude) {
        LogUtils.e(exclude);
        User user = userRepository.currentUser();
        List<Friend> friends = friendRepository.loadExclude(user.getAccount(), exclude);
        return CheckedFriend.create(friends);
    }


}
