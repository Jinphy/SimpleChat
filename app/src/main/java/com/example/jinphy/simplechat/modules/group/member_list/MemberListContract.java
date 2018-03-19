package com.example.jinphy.simplechat.modules.group.member_list;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.models.member.Member;

import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public interface MemberListContract {


    interface View extends BaseView<Presenter> {

        void whenModifyRemarkOk();

        void whenModifyAllowChatOk(String msg);

        void whenRemoveMemberOk(CheckableMember member);

        void whenRemoveMembersOk();

        void whenCreateGroupOk(Group group);


    }



    interface Presenter extends BasePresenter {

        List<CheckableMember> loadMembers(String groupNo);

        boolean isCreator(String groupNo);

        void modifyRemark(Context context, CheckableMember member, String remark);

        void modifyAllowChat(Context context, CheckableMember member);

        void removeMember(Context context, CheckableMember member);

        void removeMembers(Context context, String groupNo, List<String> members);

        String getOwner();

        Group getGroup(String groupNo);

        String getAccessToken();


        void addMembers(Context context, String groupNo, List<String> accounts);

        void createGroup(Context context, Map<String, Object> params);

        void saveMembers(Group group, List<String> members);

    }
}
