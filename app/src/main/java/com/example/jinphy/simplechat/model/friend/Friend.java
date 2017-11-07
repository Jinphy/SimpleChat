package com.example.jinphy.simplechat.model.friend;

import com.example.jinphy.simplechat.model.user.User;

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
