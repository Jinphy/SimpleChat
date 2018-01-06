package com.example.jinphy.simplechat.modules.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.constants.StringConst;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/9.
 */

public class WelcomePresenter implements WelcomeContract.Presenter {

    WelcomeContract.View view;

    public WelcomePresenter(@NonNull WelcomeContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }

    @Override
    public void start() {

    }

    @Override
    public void doAfterWelcome(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(StringConst.PREFERENCES_NAME_USER, Context.MODE_PRIVATE);
        boolean hasLogin = preferences.getBoolean(StringConst.PREFERENCES_KEY_HAS_LOGIN, false);
        boolean rememberPassword = preferences.getBoolean(StringConst.PREFERENCES_KEY_REMEMBER_PASSWORD, false);
        String account = preferences.getString(StringConst.PREFERENCES_KEY_CURRENT_ACCOUNT, null);
        String password = preferences.getString(StringConst.PREFERENCES_KEY_PASSWORD, null);
        rememberPassword = false;
        if (hasLogin && rememberPassword) {
            view.showMainActivity(account,password);
        } else {
            view.showBtn();
        }
    }

    @Override
    public boolean checkIfAccountAvailable(String account) {
        return false;
    }

}
