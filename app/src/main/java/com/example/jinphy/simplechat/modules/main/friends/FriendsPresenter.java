package com.example.jinphy.simplechat.modules.main.friends;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;
import java.util.Map;

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
    public void loadFriends(Context context) {
        String owner = userRepository.currentUser().getAccount();
        if (friendRepository.count(owner) > 1) {
            view.updateFriends(friendRepository.loadLocal(owner));
        } else {
            friendRepository.<List<Map<String,String>>>newTask()
                    .param(Api.Key.owner, owner)
                    .doOnDataOk(friendsData -> {
                        LogUtils.e(friendsData);
                        friendRepository.save(Friend.parse(friendsData.getData()));
                        view.updateFriends(friendRepository.loadLocal(owner));
                    })
                    .submit(task -> friendRepository.loadOnline(context, task));
        }
    }

    @Override
    public void loadAvatar(Context context, Friend friend) {
        userRepository.<Map<String,String>>newTask()
                .param(Api.Key.account, friend.getAccount())
                .showProgress(false)
                .autoShowNo(false)
                .doOnDataOk(avatarData -> {
                    Map<String, String> data = avatarData.getData();
                    if ("无".equals(data.get(User_.avatar.name))) {
                        friend.setAvatar("无");
                    } else {
                        friend.setAvatar("有");
                        Bitmap bitmap = StringUtils.base64ToBitmap(data.get(User_.avatar.name));
                        ImageUtil.storeAvatar(friend.getAccount(), bitmap);
                    }
                    friendRepository.update(friend);
                    view.updateView();
                })
                .submit(task -> userRepository.loadAvatar(context, task));
    }
}
