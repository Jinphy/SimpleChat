package com.example.jinphy.simplechat.modules.main.self;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class SelfPresenter implements SelfContract.Presenter {
    private SelfContract.View view;

    public SelfPresenter(@NonNull SelfContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
