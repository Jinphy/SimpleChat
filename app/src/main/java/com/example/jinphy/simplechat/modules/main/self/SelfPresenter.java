package com.example.jinphy.simplechat.modules.main.self;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.services.push.PushService;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by jinphy on 2017/8/10.
 */

public class SelfPresenter implements SelfContract.Presenter {
    private SelfContract.View view;


    private WeakReference<Context> context;
    private UserRepository userRepository;


    public SelfPresenter(Context context, @NonNull SelfContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.userRepository = UserRepository.getInstance();
        this.context = new WeakReference<>(context);
    }

    @Override
    public void start() {

    }

    @Override
    public User getUser() {
        return userRepository.currentUser();
    }

    @Override
    public void logout(Context context, String account, String accessToken) {
        userRepository.<Map<String, String>>newTask()
                .param(Api.Key.account, account)
                .param(Api.Key.accessToken, accessToken)
                .autoShowNo(false)
                .doOnDataOk(response -> {
                    userRepository.updateUser(response.getData());
                    view.whenLogout();
                    // 关闭推送服务
                    PushService.start(context,PushService.FLAG_CLOSE);
                })
                .doOnDataNo(noData -> {
                    if (Response.NO_ACCESS_TOKEN.equals(noData.getCode())) {
                        UserRepository.getInstance().logoutLocal();
                    } else {
                        App.showToast(noData.getMsg(), false);
                    }
                })
                .submit(task -> userRepository.logout(context, task));
    }

    @Override
    public void setNeedMoveUp(boolean moveUp) {
        User user = userRepository.currentUser();
        user.setNeedMoveUp(moveUp);
        userRepository.updateUser(user);
    }

    @Override
    public boolean needMoveUp() {
        return userRepository.currentUser().needMoveUp();
    }
}
