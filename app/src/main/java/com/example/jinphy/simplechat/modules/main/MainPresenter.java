package com.example.jinphy.simplechat.modules.main;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainPresenter implements MainContract.Presenter {

    private final WeakReference<Context> context;
    private MainContract.View view;

    private UserRepository userRepository;


    public MainPresenter(Context context, MainContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.context = new WeakReference<>(context);
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void findUser(String account, BaseRepository.OnDataOk<Response<User>> callback) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();

            User user = userRepository.currentUser();
            if (StringUtils.equal(user.getAccount(), account)) {
                view.showUserInfo();
                return;
            }
            Friend friend = FriendRepository.getInstance().get(user.getAccount(), account);
            if (friend != null) {
                switch (friend.getStatus()) {
                    case Friend.status_ok:
                    case Friend.status_black_listing:
                    case Friend.status_black_listed:
                        view.showFriendInfo(account);
                        return;
                }
            }

            userRepository.<User>newTask()
                    .param(Api.Key.account, account)
                    .doOnDataOk(callback::call)
                    .doOnDataNo(msg -> {
                        callback.call(new Response<>(Response.NO, msg, null));
                    })
                    .submit(task -> userRepository.findUser(context, task));
        }
    }

    @Override
    public void checkAccount(Context context) {
        userRepository.checkAccount(context);
    }
}
