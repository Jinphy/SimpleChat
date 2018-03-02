package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendRecyclerViewAdapter
        .NewFriend;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class NewFriendsPresenter implements NewFriendsContract.Presenter {

    private NewFriendsContract.View view;
    private FriendRepository friendRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public NewFriendsPresenter(NewFriendsContract.View view) {
        this.view = view;
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public NewFriendRecyclerViewAdapter getAdapter() {
        User user = userRepository.currentUser();
        List<Message> messages = messageRepository.loadSystemMsg(
                user.getAccount(), Message.TYPE_SYSTEM_ADD_FRIEND);
        List<NewFriend> newFriends = new LinkedList<>();
        for (Message message : messages) {
            Friend friend = friendRepository.get(user.getAccount(), message.getExtra());
            newFriends.add(NewFriend.create(friend, message));
        }
        return new NewFriendRecyclerViewAdapter(newFriends);
    }
}
