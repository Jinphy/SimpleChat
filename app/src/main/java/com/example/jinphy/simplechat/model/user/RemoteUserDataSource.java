package com.example.jinphy.simplechat.model.user;

import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.model.user.UserDataSource;

/**
 * Created by jinphy on 2017/11/6.
 */

public class RemoteUserDataSource implements UserDataSource {

    private static RemoteUserDataSource instance;

    private RemoteUserDataSource(){}

    public synchronized static RemoteUserDataSource getInstance(){
        if (instance == null) {
            instance = new RemoteUserDataSource();
        }
        return instance;
    }

    @Override
    public void loadUsers(UsersCallback callback) {
        
    }

    @Override
    public void getUser(long userId, UserCallback callback) {

    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void signup(User user, UserCallback callback) {

    }

    @Override
    public void login(String account, String password, UserCallback callback) {

    }

    @Override
    public void login(String account, UserCallback callback) {

    }
}
