package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendRecyclerViewAdapter
        .NewFriend;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.util.Arrays;
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
    private MessageRecordRepository recordRepository;

    public NewFriendsPresenter(NewFriendsContract.View view) {
        this.view = view;
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        userRepository = UserRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public List<NewFriend> loadNewFriends() {
        User user = userRepository.currentUser();
        List<Message> messages = messageRepository.loadSystemMsg(
                user.getAccount(), Message.TYPE_SYSTEM_ADD_FRIEND);
        Message.sort(messages);
        List<NewFriend> newFriends = new LinkedList<>();
        for (Message message : messages) {
            Friend friend = friendRepository.get(user.getAccount(), message.getExtra());
            if (friend == null) {
                messageRepository.delete(message);
                messages.remove(message);
            } else {
                newFriends.add(NewFriend.create(friend, message));
            }
        }
        return newFriends;
    }

    @Override
    public void updateMsg(List<Message> messages) {
        if (messages.size() > 0) {
            messageRepository.update(messages);
            Message message = messages.get(0);
            Friend friend = friendRepository.get(message.getOwner(), Friend.system);
            MessageRecord record = recordRepository.get(message.getOwner(), friend);
            record.setNewMsgCount(record.getNewMsgCount() - messages.size());
            recordRepository.saveOrUpdate(record);
        }
    }
}
