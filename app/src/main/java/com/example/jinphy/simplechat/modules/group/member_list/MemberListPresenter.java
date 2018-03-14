package com.example.jinphy.simplechat.modules.group.member_list;

import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/14.
 */

public class MemberListPresenter implements MemberListContract.Presenter {

    private MemberListContract.View view;
    private MemberRepository memberRepository;
    private UserRepository userRepository;

    public MemberListPresenter(MemberListContract.View view) {

        this.view = view;
        memberRepository = MemberRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }


    @Override
    public void start() {

    }

    @Override
    public List<Member> loadMembers(String groupNo) {
        User user = userRepository.currentUser();
        return memberRepository.get(groupNo, user.getAccount());
    }
}
