package com.example.jinphy.simplechat.models.member;

import android.content.Context;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;
import java.util.Map;

import io.objectbox.Box;

/**
 *
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
    public void save(Member... members) {
        if (members.length == 0) {
            return;
        }
        for (Member member : members) {
            if (member != null) {
                Member old = memberBox.query()
                        .equal(Member_.groupNo, member.getGroupNo())
                        .equal(Member_.owner, member.getOwner())
                        .build().findFirst();
                if (old != null) {
                    member.setId(old.getId());
                } else {
                    member.setId(0);
                }
            }
        }
        memberBox.put(members);
    }

    @Override
    public void save(List<Member> members) {
        if (members == null || members.size() == 0) {
            return;
        }
        save(members.toArray(new Member[members.size()]));
    }

    @Override
    public Member get(String groupNo, String account, String owner) {
        return memberBox.query()
                .filter(member -> {
                    return StringUtils.equal(groupNo, member.getGroupNo())
                            && StringUtils.equal(account, member.getPerson().getTarget()
                            .getAccount())
                            && StringUtils.equal(owner, member.getOwner());
                })
                .build()
                .findFirst();
    }

    @Override
    public List<Member> get(String groupNo, String owner) {
        return memberBox.query()
                .equal(Member_.groupNo, groupNo)
                .equal(Member_.owner, owner)
                .build().find();
    }

    @Override
    public void loadOnline(Context context, Task<List<Map<String, String>>> task) {
        Api.<List<Map<String, String>>>common(context)
                .hint("正在加载...")
                .path(Api.Path.getMembers)
                .dataType(Api.Data.MAP_LIST)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    @Override
    protected <T> void handleBuilder(ApiInterface<Response<T>> api, Task<T> task) {
        api.showProgress(task.isShowProgress())
                .useCache(task.isUseCache())
                .autoShowNo(task.isAutoShowNo())
                .showProgress(task.isShowProgress())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk() == null ? null : response -> task.getOnDataOk()
                        .call(response))
                .onResponseNo(task.getOnDataNo() == null ? null : response -> task.getOnDataNo()
                        .call(TYPE_CODE))
                .onError(task.getOnDataNo() == null ? null : e -> task.getOnDataNo().call
                        (TYPE_ERROR))
                .onFinal(task.getOnFinal() == null ? null : task.getOnFinal()::call);
    }
}
