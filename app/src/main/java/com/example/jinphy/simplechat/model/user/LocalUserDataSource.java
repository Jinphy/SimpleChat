package com.example.jinphy.simplechat.model.user;

import android.content.Context;

import com.example.jinphy.simplechat.application.DBApplication;
import com.example.jinphy.simplechat.model.user.User;
import com.example.jinphy.simplechat.model.user.UserDataSource;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.rx.RxBoxStore;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jinphy on 2017/11/6.
 */

public class LocalUserDataSource implements UserDataSource {

    private static LocalUserDataSource instance;
    private static BoxStore boxStore;

    private LocalUserDataSource() {

    }

    public synchronized static LocalUserDataSource getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new LocalUserDataSource();

        getBoxStore();

        return instance;
    }



    public static BoxStore getBoxStore() {
        if (boxStore != null) {
            return boxStore;
        }
        boxStore =  ((DBApplication) DBApplication.instance()).getBoxStore();
        return boxStore;
    }

    @Override
    public void loadUsers(UsersCallback callback) {
        Flowable.just(getBoxStore())
                .flatMap(boxStore -> Flowable.just(boxStore.boxFor(User.class)))
                .map(userBox -> userBox.query().build().find())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(users -> callback.onSucceed(users))
                .doOnError(ex -> callback.onFail(ex.getMessage()))
                .subscribe();
    }

    @Override
    public void getUser(long userId, UserCallback callback) {
        Flowable.just(userId)
                .map(id -> getBoxStore().boxFor(User.class).get(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(user -> callback.onSucceed(user))
                .doOnError(ex -> callback.onFail(ex.getMessage()))
                .subscribe();
    }

    @Override
    public void saveUser(User user) {
        getBoxStore().boxFor(User.class).put(user);
    }

    @Override
    public void signup(User user, UserCallback callback) {
        // do nothing
    }

    @Override
    public void login(String account, String password, UserCallback callback) {
        // do nothing
    }

    @Override
    public void login(String account, UserCallback callback) {
        // do nothing
    }
}
