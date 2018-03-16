package com.example.jinphy.simplechat.modules.group.member_list;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.models.member.Member;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public interface MemberListContract {


    interface View extends BaseView<Presenter> {

        void whenModifyRemarkOk();

        void whenModifyAllowChatOk(String msg);
    }



    interface Presenter extends BasePresenter {

        List<CheckableMember> loadMembers(String groupNo);

        boolean isCreator(String groupNo);

        void modifyRemark(Context context, CheckableMember member, String remark);

        void modifyAllowChat(Context context, CheckableMember member);

    }
}
