package com.example.jinphy.simplechat.model.user;

import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * Created by jinphy on 2017/11/6.
 */

public interface UserDataSource {
    interface UsersCallback{
        void onSucceed(List<User> users);

        void onFail(String msg);
    }
    interface UserCallback{
        void onSucceed(User user);

        void onFail(String msg);
    }


    void loadUsers(@NonNull UsersCallback callback);

    void getUser(@NonNull long userId, UserCallback callback);

    void saveUser(@NonNull User user);

    void signup(User user, UserCallback callback);

    /**
     * 密码方式登录
     * */
    void login(String account,String password, UserCallback callback);

    /**
     * 验证码方式登录
     * */
    void login(String account, UserCallback callback);
}
