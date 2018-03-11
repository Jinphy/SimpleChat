package com.example.jinphy.simplechat.models.group;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by Jinphy on 2018/3/5.
 */

public interface GroupDataSource {


    void createGroup(Context context, BaseRepository.Task<Map<String, String>> task);

    List<Group> loadLocal(String owner,boolean showSearchResult);

    void loadOnline(Context context, BaseRepository.Task<List<Map<String,String>>> task);

    void search(Context context, BaseRepository.Task<List<Map<String, String>>> task);


    void getOnline(Context context, String owner, String groupNo, Runnable... whenOk);


    void saveMyGroup(Group... groups);

    void saveMyGroup(List<Group> groups);

    void saveSearch(Group... groups);

    void saveSearch(List<Group> groups);

    void deleteSearch();


    Group get(String groupNo, String owner);
}
