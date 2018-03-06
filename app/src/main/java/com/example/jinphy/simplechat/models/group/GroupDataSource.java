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

    List<Group> loadLocal(String owner);

    void loadOnline(Context context, BaseRepository.Task<List<Map<String,String>>> task);


    void getOnline(Context context, String owner, String groupNo, Runnable... whenOk);


    void save(Group... group);


    Group get(String groupNo, String owner);
}
