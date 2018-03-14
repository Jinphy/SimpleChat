package com.example.jinphy.simplechat.modules.group.member_list;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.member.Member;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public interface MemberListContract {


    interface View extends BaseView<Presenter> {
    }



    interface Presenter extends BasePresenter {

        List<Member> loadMembers(String groupNo);
    }
}
