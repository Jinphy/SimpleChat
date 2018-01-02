package com.example.jinphy.simplechat.model.user;

import com.example.jinphy.simplechat.model.friend.Friend;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.reactivex.annotations.NonNull;

/**
 * Created by jinphy on 2017/11/6.
 */

@Entity
public class User implements Serializable{
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

    /**
     * DESC：由三部分组成
     *  1、设备id：  IMEI
     *  2、账号：    account
     *  3、登录时间： 登录时生成(time)，时间由System.currentTimeMillis() 获取
     *
     *  把上面三部分拼接成一个字符串，格式为： IMEI&account&time
     *  然后在经过编码加密生成最终的accessToken，该字段有后台生成，在每次执行登录操作时更新
     *
     * Created by jinphy, on 2018/1/2, at 19:52
     */
    protected String accessToken;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
