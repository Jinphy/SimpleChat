package com.example.jinphy.simplechat.modules.main;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;


    public MainPresenter(MainContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
