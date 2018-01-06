package com.example.jinphy.simplechat.models.verification_code;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;

/**
 * DESC:
 * Created by jinphy on 2018/1/6.
 */

public interface CodeDataSource<T> {

    void getCode(Context context, BaseRepository.Task<T> task);

    void verify(Context context, BaseRepository.Task<T> task);

}
