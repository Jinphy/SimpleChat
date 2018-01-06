package com.example.jinphy.simplechat.modules.main.friends;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsPresenter implements FriendsContract.Presenter {

    FriendsContract.View view;
    private List<Friend> friends;

    public FriendsPresenter(@NonNull FriendsContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }

    @Override
    public void start() {

    }

    @Override
    public List<Friend> loadFriends() {

        friends = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            friends.add(new Friend());
        }
        return friends;
    }

    @Override
    public FriendsRecyclerViewAdapter getAdapter() {

        return  new FriendsRecyclerViewAdapter(loadFriends());
    }
}
