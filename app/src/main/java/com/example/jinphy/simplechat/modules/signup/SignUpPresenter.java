package com.example.jinphy.simplechat.modules.signup;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/9.
 */

public class SignUpPresenter implements SignUpContract.Presenter {

    private final SignUpContract.View view;


    public SignUpPresenter(@NonNull SignUpContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
