package com.example.jinphy.simplechat.modules.modify_friend_info;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class ModifyFriendInfoPresenter implements ModifyFriendInfoContract.Presenter {


    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private MessageRecordRepository recordRepository;
    private FriendRepository friendRepository;

    private final ModifyFriendInfoContract.View view;

    public ModifyFriendInfoPresenter(ModifyFriendInfoContract.View view) {
        this.view = view;
        userRepository = UserRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public Friend getFriend(String account) {
        User user = userRepository.currentUser();
        return friendRepository.get(user.getAccount(), account);
    }

    @Override
    public void addFriend(Context context, Map<String, Object> params) {
        User user = userRepository.currentUser();
        params.put(Api.Key.requestAccount, user.getAccount());
        friendRepository.<Map<String,String>>newTask(params)
                .doOnDataOk(okData -> view.afterAddFriend())
                .submit(task -> friendRepository.addFriend(context, task));
    }

    @Override
    public void saveFriend(Friend friend) {
        friendRepository.save(friend);
    }

    @Override
    public void modifyRemark(Context context, Map<String, Object> params) {
        friendRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> view.afterModifyRemark())
                .submit(task -> friendRepository.modifyRemark(context, task));
    }

    @Override
    public void modifyStatus(Context context, Map<String, Object> params) {
        friendRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> view.afterModifyStatus())
                .submit(task -> friendRepository.modifyStatus(context, task));
    }

    @Override
    public void deleteFriend(Context context, Map<String, Object> params) {
        friendRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> view.afterDeleteFriend())
                .submit(task -> friendRepository.deleteFriend(context, task));
    }

    @Override
    public void deleteFriendLocal(Friend friend) {
        User user = userRepository.currentUser();
        recordRepository.delete(user.getAccount(), friend);
        messageRepository.delete(user.getAccount(), friend.getAccount());
        friendRepository.delete(friend);
    }
}
