package com.example.jinphy.simplechat.modules.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.constants.StringConst;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/9.
 */

public class WelcomePresenter implements WelcomeContract.Presenter {

    WelcomeContract.View view;
    UserRepository userRepository;

    public WelcomePresenter(@NonNull WelcomeContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void doAfterWelcome(Context context) {
        if (userRepository.needToLogin()) {
            view.showBtn();
        } else {
            view.showMainActivity();
        }
    }

    @Override
    public boolean checkIfAccountAvailable(String account) {
        return false;
    }

}
