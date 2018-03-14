package com.example.jinphy.simplechat.modules.group.group_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.time.temporal.ValueRange;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class ModifyGroupPresenter implements ModifyGroupContract.Presenter {

    private ModifyGroupContract.View view;

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private MessageRecordRepository recordRepository;
    private MessageRepository messageRepository;

    public ModifyGroupPresenter(ModifyGroupContract.View view) {
        this.view = view;
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
    }


    @Override
    public void start() {

    }

    @Override
    public Group getGroup(String groupNo) {
        User user = userRepository.currentUser();
        return groupRepository.get(groupNo, user.getAccount());
    }

    @Override
    public int countMembers(Group group) {
        return (int) memberRepository.count(group.getGroupNo(), group.getOwner());
    }

    @Override
    public void updateGroup(Group group) {
        groupRepository.update(group);
    }

    @Override
    public void modifyAvatar(Context context, Group group, Bitmap bitmap) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.avatar, StringUtils.bitmapToBase64(bitmap))
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    ImageUtil.storeAvatar(group.getGroupNo(), bitmap);
                    view.whenModifyAvatarOk();
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void modifyName(Context context, Group group,String name) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.name, name)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyNameOk();
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }


    @Override
    public void modifyMaxCount(Context context, Group group, int maxCount) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.creator, group.getCreator())
                .param(Api.Key.maxCount,maxCount)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyMaxCountOk(maxCount);
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void modifyAutoAdd(Context context, Group group, boolean autoAdd) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.creator, group.getCreator())
                .param(Api.Key.autoAdd,autoAdd)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyAutoAddOk(autoAdd);
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void modifyShowMemberName(Context context, Group group, boolean showMemberName) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.showMemberName, showMemberName)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyShowMemberNameOk(showMemberName);
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void modifyKeepSilent(Context context, Group group, boolean keepSilent) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.keepSilent, keepSilent)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyKeepSilentOk(keepSilent);
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void modifyRejectMsg(Context context, Group group, boolean rejectMsg) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.owner, group.getOwner())
                .param(Api.Key.rejectMsg, rejectMsg)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                    view.whenModifyRejectMsgOk(rejectMsg);
                })
                .submit(task -> groupRepository.modifyGroup(context, task));
    }

    @Override
    public void joinGroup(Context context, Group group,String extraMsg) {
        User user = userRepository.currentUser();
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.creator, group.getCreator())
                .param(Api.Key.account, user.getAccount())
                .param(Api.Key.extraMsg, extraMsg)
                .doOnDataOk(okData -> {
                    App.showToast(okData.getMsg(), false);
                })
                .submit(task -> groupRepository.joinGroup(context, task));

    }

    @Override
    public void breakGroup(Context context, Group group) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.operator, group.getOwner())
                .doOnDataOk(okData -> {
                    groupRepository.remove(group);
                    memberRepository.removeForGroup(group);
                    recordRepository.delete(group.getOwner(), group);
                    messageRepository.delete(group.getOwner(), group.getGroupNo());
                    EventBus.getDefault().post(new EBUpdateView("FromModifyGroupFragment"));
                    view.whenBreakGroupOk();
                })
                .submit(task -> groupRepository.exitGroup(context, task));
    }

    @Override
    public void exitGroup(Context context, Group group) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, group.getGroupNo())
                .param(Api.Key.operator, group.getOwner())
                .param(Api.Key.account, group.getOwner())
                .doOnDataOk(okData -> {
                    groupRepository.remove(group);
                    memberRepository.removeForGroup(group);
                    recordRepository.delete(group.getOwner(), group);
                    messageRepository.delete(group.getOwner(), group.getGroupNo());
                    EventBus.getDefault().post(new EBUpdateView("FromModifyGroupFragment"));
                    view.whenExitGroupOk();
                })
                .submit(task -> groupRepository.exitGroup(context, task));

    }

    @Override
    public void deleteGroupLocal(Group group) {
        groupRepository.remove(group);
        memberRepository.removeForGroup(group);
        recordRepository.delete(group.getOwner(), group);
        messageRepository.delete(group.getOwner(), group.getGroupNo());
        EventBus.getDefault().post(new EBUpdateView("FromModifyGroupFragment"));
        view.whenDeleteGroupLocalOk();
    }
}
