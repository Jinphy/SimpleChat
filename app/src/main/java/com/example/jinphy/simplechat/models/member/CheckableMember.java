package com.example.jinphy.simplechat.models.member;

import com.example.jinphy.simplechat.models.friend.Friend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/15.
 */
public class CheckableMember {
    private Member member;

    boolean isChecked;


    public static List<CheckableMember> create(List<Member> members) {
        if (members == null) {
            return new LinkedList<>();
        }
        List<CheckableMember> result = new ArrayList<>(members.size());
        for (Member member : members) {
            result.add(new CheckableMember(member));
        }
        return result;
    }



    public CheckableMember(Member member) {
        this.member = member;
    }

    public CheckableMember(Member member, boolean isChecked) {
        this.member = member;
        this.isChecked = isChecked;
    }

    public String getName() {
        return member.getName();
    }

    public String getAccount() {
        return member.getAccount();
    }

    public String getGroupNo() {
        return member.getGroupNo();
    }

    public String getOwner() {
        return member.getOwner();
    }

    public boolean isAllowChat() {
        return member.isAllowChat();
    }

    public String getStatus() {
        return member.getStatus();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public Member getMember() {
        return member;
    }

    public Friend getPerson() {
        return member.getPerson().getTarget();
    }

}

