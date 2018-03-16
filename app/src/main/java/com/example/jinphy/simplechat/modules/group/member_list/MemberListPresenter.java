package com.example.jinphy.simplechat.modules.group.member_list;

import android.content.Context;
import android.text.TextUtils;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MemberListPresenter implements MemberListContract.Presenter {

    private MemberListContract.View view;
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private FriendRepository friendRepository;

    public MemberListPresenter(MemberListContract.View view) {

        this.view = view;
        memberRepository = MemberRepository.getInstance();
        userRepository = UserRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
    }


    @Override
    public void start() {

    }

    @Override
    public List<CheckableMember> loadMembers(String groupNo) {
        User user = userRepository.currentUser();
        List<Member> members = memberRepository.get(groupNo, user.getAccount());
        return CheckableMember.create(members);
    }

    @Override
    public boolean isCreator(String groupNo) {
        User user = userRepository.currentUser();
        Group group = groupRepository.get(groupNo, user.getAccount());
        return StringUtils.equal(group.getCreator(), group.getOwner());
    }

    @Override
    public void modifyRemark(Context context, CheckableMember member, String remark) {
        if (TextUtils.isEmpty(remark)) {
            App.showToast("请输入备注信息！", false);
            return;
        }
        if (member == null) {
            return;
        }
        friendRepository.<Map<String, String>>newTask()
                .param(Api.Key.account, member.getAccount())
                .param(Api.Key.owner, member.getOwner())
                .param(Api.Key.remark, remark)
                .doOnDataOk(okData -> {
                    member.getPerson().setRemark(remark);
                    friendRepository.update(member.getPerson());
                    view.whenModifyRemarkOk();
                })
                .submit(task -> friendRepository.modifyRemark(context, task));
    }

    @Override
    public void modifyAllowChat(Context context, CheckableMember member) {
        if (member == null) {
            return;
        }
        memberRepository.<String>newTask()
                .param(Api.Key.groupNo, member.getGroupNo())
                .param(Api.Key.account, member.getAccount())
                .param(Api.Key.creator, member.getOwner())
                .param(Api.Key.allowChat, !member.isAllowChat())
                .doOnDataOk(okData -> {
                    member.getMember().setAllowChat(!member.isAllowChat());
                    memberRepository.update(member.getMember());
                    view.whenModifyAllowChatOk(okData.getMsg());
                })
                .submit(task -> memberRepository.modifyAllowChat(context, task));
    }
}
