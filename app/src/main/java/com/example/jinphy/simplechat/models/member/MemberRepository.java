package com.example.jinphy.simplechat.models.member;

import android.content.Context;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
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
                memberBox.put(member);
            }
        }
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
        List<Member> members = memberBox.query()
                .filter(member -> StringUtils.equal(groupNo, member.getGroupNo())
                        && StringUtils.equal(account, member.getPerson().getTarget().getAccount())
                        && StringUtils.equal(owner, member.getOwner()))
                .build()
                .find();
        if (members != null && members.size() > 0) {
            return members.get(0);
        }
        return null;
    }

    @Override
    public List<Member> get(String groupNo, String owner) {
        return memberBox.query()
                .equal(Member_.groupNo, groupNo)
                .equal(Member_.owner, owner)
                .build().find();
    }

    /**
     * DESC: 统计当前账号的指定群中的成员数量
     * Created by jinphy, on 2018/3/13, at 18:43
     */
    public long count(String groupNo, String owner) {
        return memberBox.query()
                .equal(Member_.groupNo, groupNo)
                .equal(Member_.owner, owner)
                .build().count();
    }

    /**
     * DESC: 删除某个群聊的所有成员
     * Created by jinphy, on 2018/3/14, at 9:41
     */
    @Override
    public void removeForGroup(Group group) {
        String groupNo = group.getGroupNo();
        String owner = group.getOwner();
        List<Member> members = get(groupNo, owner);
        for (Member member : members) {
            Friend friend = member.getPerson().getTarget();
            FriendRepository.getInstance().subGroupCount(friend);
            memberBox.remove(member);
        }
    }


    /**
     * DESC: 将指定成员从指定的群聊中删除
     * Created by jinphy, on 2018/3/14, at 9:44
     */
    @Override
    public void removeFromGroup(Group group, String account) {
        String groupNo = group.getGroupNo();
        String owner = group.getOwner();
        Member member = get(groupNo, account, owner);
        Friend friend = member.getPerson().getTarget();
        FriendRepository.getInstance().subGroupCount(friend);
        memberBox.remove(member);
    }

    @Override
    public void loadOnline(Context context, Task<List<Map<String, String>>> task) {
        Api.<List<Map<String, String>>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在加载...")
                .path(Api.Path.getMembers)
                .dataType(Api.Data.MAP_LIST)
                .request();
    }
}
