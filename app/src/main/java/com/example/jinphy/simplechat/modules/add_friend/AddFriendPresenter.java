package com.example.jinphy.simplechat.modules.add_friend;

import android.content.Context;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/1/15.
 */

public class AddFriendPresenter implements AddFriendContract.Presenter {

    private final WeakReference<Context> context;
    private final AddFriendContract.View view;
    private UserRepository userRepository;
    private FriendRepository friendRepository;

    public AddFriendPresenter(Context context, AddFriendContract.View view) {
        this.context = new WeakReference<>(context);
        this.view = view;
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
    }


    @Override
    public void start() {

    }

    @Override
    public void addFriend(Context context, Map<String, Object> params) {
        friendRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.finish();

                })
                .submit(task -> friendRepository.addFriend(context, task));
    }

    @Override
    public void saveFriend(Friend friend) {
        friendRepository.save(friend);
    }

    @Override
    public String getCurrentAccount() {
        return userRepository.currentUser().getAccount();
    }
}
