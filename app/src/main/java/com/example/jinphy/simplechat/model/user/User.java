package com.example.jinphy.simplechat.model.user;

import com.example.jinphy.simplechat.model.friend.Friend;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.reactivex.annotations.NonNull;

/**
 * Created by jinphy on 2017/11/6.
 */

@Entity
public class User {
    @Id
    protected long id;

    @NonNull
    protected String account;

    protected String name;

    @NonNull
    protected String password;

    @NonNull
    protected long date;

    protected String avatarUrl;

    protected String status;

    public ToMany<Friend> friends;

    public User(){}

    public User(int id, String account, String password, long date, String avatarUrl) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.date = date;
        this.avatarUrl = avatarUrl;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public long getDate() {
        return date;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public ToMany<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ToMany<Friend> friends) {
        this.friends = friends;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
