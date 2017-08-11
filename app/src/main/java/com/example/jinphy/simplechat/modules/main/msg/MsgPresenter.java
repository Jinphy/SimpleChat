package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgPresenter implements MsgContract.Presenter {
    MsgContract.View view;

    public MsgPresenter(@NonNull MsgContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
