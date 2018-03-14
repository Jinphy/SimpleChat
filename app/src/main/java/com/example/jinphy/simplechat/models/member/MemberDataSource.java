package com.example.jinphy.simplechat.models.member;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.group.Group;

import java.util.List;
import java.util.Map;

/**
 * Created by Jinphy on 2018/3/6.
 */

public interface MemberDataSource {

    void save(Member... members);

    void save(List<Member> members);

    void loadOnline(Context context, BaseRepository.Task<List<Map<String, String>>> task);

    void removeForGroup(Group group);

    void removeFromGroup(Group group,String account);

    Member get(String groupNo, String account, String owner);

    List<Member> get(String groupNo, String owner);
}
