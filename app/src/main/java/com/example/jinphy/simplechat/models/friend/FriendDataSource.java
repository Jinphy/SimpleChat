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

    List<Friend> loadLocal(String owner, String... account);


    void loadOnline(Context context, BaseRepository.Task<List<Map<String,String>>> task);

    void getOnline(Context context, String owner, String account);

    Friend get(String owner, String account);

    void addFriend(Context context, BaseRepository.Task<Map<String, String>> task);

    void modifyRemark(Context context, BaseRepository.Task<Map<String, String>> task);

    void modifyStatus(Context context, BaseRepository.Task<Map<String, String>> task);

    void deleteFriend(Context context, BaseRepository.Task<Map<String, String>> task);

    long count(String owner);

    void save(List<Friend> friends);

    void save(Friend... friends);

    void update(Friend friend);

    void delete(Friend friend);

    void addSystemFriendLocal(String owner);

}
