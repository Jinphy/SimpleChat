package com.example.jinphy.simplechat.modules.main.friends;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class FriendsPresenter implements FriendsContract.Presenter {

    FriendsContract.View view;

    public FriendsPresenter(@NonNull FriendsContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
