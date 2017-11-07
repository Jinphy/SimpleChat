package com.example.jinphy.simplechat.modules.welcome;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.user.User;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface WelcomeContract {

    interface View extends BaseView<Presenter>{
        void showLoginView(android.view.View view);

        void showSignUpView(android.view.View view);

        void showMainActivity();

        void showAnimator();

        void showBtn();
    }

    interface Presenter extends BasePresenter{

        void doAfterWelcome(Context context);

        boolean checkIfAccountAvailable(String account);

        void doSignUp(User newUser);
    }
}
