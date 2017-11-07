package com.example.jinphy.simplechat.model.user;

/**
 * Created by jinphy on 2017/11/6.
 */

public class UserRepository implements UserDataSource {

    private static UserRepository instance;

    private UserDataSource local;
    private UserDataSource remote;

    private UserRepository(){
        local = LocalUserDataSource.getInstance();
        remote = RemoteUserDataSource.getInstance();
    }

    public static synchronized UserRepository getInstance(){
        if (instance == null) {
            instance = new UserRepository();
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
        local.saveUser(user);
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
