package com.example.jinphy.simplechat.models.member;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.friend.FriendRepository;

import io.objectbox.Box;

/**
 * Created by Jinphy on 2018/3/6.
 */

public class MemberRepository extends BaseRepository implements MemberDataSource{

    private Box<Member> memberBox;


    private static class InstanceHolder{
        static final MemberRepository DEFAULT = new MemberRepository();
    }

    public static MemberRepository getInstance() {
        return MemberRepository.InstanceHolder.DEFAULT;
    }

    public MemberRepository() {
        memberBox = App.boxStore().boxFor(Member.class);
    }







    @Override
    protected <T> void handleBuilder(ApiInterface<Response<T>> api, Task<T> task) {

    }
}
