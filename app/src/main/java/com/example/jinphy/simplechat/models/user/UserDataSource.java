package com.example.jinphy.simplechat.models.user;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;

/**
 *
 * Created by jinphy on 2017/11/6.
 */
public interface UserDataSource<T> {


    void findUser(Context context, BaseRepository.Task<T> task);

    void login(Context context, BaseRepository.Task<T> task);

    void signUp(Context context, BaseRepository.Task<T> task);

    void saveUser(User user);

}
