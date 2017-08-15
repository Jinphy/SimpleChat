package com.example.jinphy.simplechat.modules.login;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;

    public LoginPresenter(@NonNull LoginContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }



    @Override
    public void start() {

    }
}
