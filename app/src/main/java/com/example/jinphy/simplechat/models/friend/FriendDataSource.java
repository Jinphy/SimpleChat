package com.example.jinphy.simplechat.models.friend;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;

import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public interface FriendDataSource {

    List<Friend> loadLocal(String owner);

    void loadOnline(Context context, BaseRepository.Task<List<Friend>> task);

    Friend get(String owner, String account);

    void addFriend(Context context, BaseRepository.Task<Map<String, String>> task);

    long count(String owner);

    void save(List<Friend> friends);

}
