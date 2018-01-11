package com.example.jinphy.simplechat.models.friend;

import com.example.jinphy.simplechat.models.user.User;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import io.reactivex.annotations.NonNull;

/**
 * Created by jinphy on 2017/8/10.
 */

@Entity
public class Friend {

    @Id protected long id;

    @NonNull
    protected String account;

    protected String name;

    protected String avatar;

    protected String remark;

    protected String sex;

    /**
     * DESC: 一个好友只属于一个用户的，所以是一对一的关系，即一个Friend -> 一个User
     * Created by jinphy, on 2018/1/3, at 13:32
     */
    public ToOne<User> user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(ToOne<User> user) {
        this.user = user;
    }
}
