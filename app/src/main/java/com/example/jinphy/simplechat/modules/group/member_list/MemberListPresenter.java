package com.example.jinphy.simplechat.modules.group.member_list;

import android.content.Context;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

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
    private MessageRepository messageRepository;
    private MessageRecordRepository recordRepository;

    public MemberListPresenter(MemberListContract.View view) {

        this.view = view;
        memberRepository = MemberRepository.getInstance();
        userRepository = UserRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
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
    public String getOwner() {
        return userRepository.currentUser().getAccount();
    }

    @Override
    public String getAccessToken() {
        return userRepository.currentUser().getAccessToken();
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

    @Override
    public void removeMember(Context context, CheckableMember member) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, member.getGroupNo())
                .param(Api.Key.operator, member.getOwner())
                .param(Api.Key.account, member.getAccount())
                .doOnDataOk(okData -> {
                    Group group = groupRepository.get(member.getGroupNo(), member.getOwner());
                    memberRepository.removeFromGroup(group, member.getAccount());
                    EventBus.getDefault().post(new EBUpdateView(MemberListFragment.TAG));
                    view.whenRemoveMemberOk(member);
                })
                .submit(task -> groupRepository.exitGroup(context, task));
    }



    @Override
    public void removeMembers(Context context, String groupNo, List<String> members) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, groupNo)
                .param(Api.Key.operator, getOwner())
                .param(Api.Key.accounts, GsonUtils.toJson(members))
                .doOnDataOk(okData -> {
                    Group group = groupRepository.get(groupNo, getOwner());
                    for (String member : members) {
                        memberRepository.removeFromGroup(group, member);
                    }
                    EventBus.getDefault().post(new EBUpdateView(MemberListFragment.TAG));
                    view.whenRemoveMembersOk();
                })
                .submit(task -> groupRepository.removeMembers(context, task));
    }

    @Override
    public Group getGroup(String groupNo) {
        User user = userRepository.currentUser();
        return groupRepository.get(groupNo, user.getAccount());
    }

    @Override
    public void addMembers(Context context, String groupNo, List<String> accounts) {
        User user = userRepository.currentUser();
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, groupNo)
                .param(Api.Key.operator, user.getAccount())
                .param(Api.Key.accounts, GsonUtils.toJson(accounts))
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    for (String account : accounts) {
                        Friend friend = friendRepository.get(user.getAccount(), account);
                        memberRepository.saveNew(friend, groupNo);
                    }
                    EventBus.getDefault().post(new EBUpdateView());
                })
                .doOnDataNo(noData -> {
                    if (noData != null) {
                        App.showToast(noData.getMsg(), false);
                    } else {
                        App.showToast("请求异常！", false);
                    }
                })
                .submit(task -> groupRepository.addMembers(context, task));
    }

    @Override
    public void createGroup(Context context, Map<String, Object> params) {
        groupRepository.<Map<String,String>>newTask(params)
                .doOnDataOk(okData -> {
                    Group group = Group.parse(okData.getData());
                    groupRepository.saveMyGroup(group);
                    createMsg(group, okData.getData().get("msg"));
                    view.whenCreateGroupOk(group);
                })
                .submit(task -> groupRepository.createGroup(context, task));
    }

    private void createMsg(Group group, String msg) {
        // 生成消息
        Message message = new Message();
        message.setCreateTime(System.currentTimeMillis()+"");
        message.setSourceType(Message.SEND);
        message.setContent(msg);
        message.setContentType(Message.TYPE_CHAT_TEXT);
        message.setWith(group.getGroupNo());
        message.setOwner(group.getOwner());
        message.setExtra(group.getOwner());// 群消息时，该字段表示信息发送者

        // 生成消息记录
        messageRepository.saveSend(message);
        MessageRecord record = new MessageRecord();
        record.setNewMsgCount(0);
        record.setLastMsg(message);
        record.setWith(group);
        record.setOwner(group.getOwner());
        record.setId(0);
        recordRepository.saveOrUpdate(record);
    }

    @Override
    public void saveMembers(Group group, List<String> members) {
        if (members == null || members.size() == 0) {
            return;
        }
        LogUtils.e("member size: " + members.size());
        User user = userRepository.currentUser();
        for (String m : members) {
            Member member = new Member();
            member.setStatus(Member.STATUS_OK);
            member.setAllowChat(true);
            member.setGroupNo(group.getGroupNo());
            member.setOwner(user.getAccount());
            member.setPerson(friendRepository.get(user.getAccount(), m));
            memberRepository.save(member);
        }
    }


}
