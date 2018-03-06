package com.example.jinphy.simplechat.models.member;

import android.text.TextUtils;

import com.example.jinphy.simplechat.models.friend.Friend;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by Jinphy on 2018/3/5.
 */

@Entity
public class Member {

    @Id
    private long id;

    private ToOne<Friend> person;

    /**
     * DESC: 是否允许发言
     * Created by Jinphy, on 2018/3/5, at 16:49
     */
    private boolean allowChat;

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

    public boolean isAllowChat() {
        return allowChat;
    }

    public void setAllowChat(boolean allowChat) {
        this.allowChat = allowChat;
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
}
