package com.example.jinphy.simplechat.models.member;

import android.text.TextUtils;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.friend.Friend_;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by Jinphy on 2018/3/5.
 */

@Entity
public class Member {

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_OK = "ok";

    @Id
    private long id;

    private ToOne<Friend> person;

    /**
     * DESC: 是否允许发言
     * Created by Jinphy, on 2018/3/5, at 16:49
     */
    private boolean allowChat;


    /**
     * DESC: 成员所属的群号
     * Created by jinphy, on 2018/3/10, at 10:50
     */
    private String groupNo;

    /**
     * DESC: 成员拥有者
     *
     * Created by jinphy, on 2018/3/10, at 10:51
     */
    private String owner;


    private String status;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ToOne<Friend> getPerson() {
        return person;
    }

    public void setPerson(ToOne<Friend> person) {
        this.person = person;
    }

    public void setPerson(Friend person) {
        if (person == null) {
            return;
        }
        this.person.setTarget(person);
    }

    public boolean isAllowChat() {
        return allowChat;
    }

    public void setAllowChat(boolean allowChat) {
        this.allowChat = allowChat;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //---------------------------------------------------------
    public String getName() {
        Friend person = this.person.getTarget();
        if (!TextUtils.isEmpty(person.getRemark())) {
            return person.getRemark();
        } else if (!TextUtils.isEmpty(person.getName())) {
            return person.getName();
        }
        return "暂无昵称";
    }

    public String getAccount() {
        return person.getTarget().getAccount();
    }

    public static List<Member> parse(List<Map<String, String>> maps) {
        List<Member> members = new LinkedList<>();
        if (maps != null || maps.size() > 0) {
            for (Map<String, String> map : maps) {
                members.add(Member.parse(map));
            }
        }
        return members;
    }

    public static Member parse(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        String owner = UserRepository.getInstance().currentUser().getAccount();
        Friend friend = FriendRepository.getInstance().get(owner, map.get(Friend_.account.name));

        Member member = new Member();
        member.setId(0);
        member.setAllowChat(Boolean.valueOf(map.get(Member_.allowChat.name)));
        member.setGroupNo(map.get(Member_.groupNo.name));
        member.setStatus(map.get(Member_.status.name));
        member.setOwner(owner);
        member.setPerson(friend);
        return member;
    }

}
