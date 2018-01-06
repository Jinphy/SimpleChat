package com.example.jinphy.simplechat.models.user;

import com.example.jinphy.simplechat.models.friend.Friend;

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

    public static final String ID = "id";
    public static final String ACCOUNT = "account";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String DATE = "date";
    public static final String SEX = "sex";
    public static final String AVATAR_URL = "avatarUrl";
    public static final String STATUS = "status";
    public static final String ACCESS_TOKEN = "accessToken";


    @Id
    protected long id;

    /**
     * DESC: 账号
     * Created by jinphy, on 2018/1/3, at 14:22
     */
    @NonNull
    protected String account;

    /**
     * DESC: 用户名
     * Created by jinphy, on 2018/1/3, at 14:22
     */
    protected String name;

    /**
     * DESC: 用户密码
     * Created by jinphy, on 2018/1/3, at 14:23
     */
    @NonNull
    protected String password;

    /**
     * DESC: 账号创建日期
     * Created by jinphy, on 2018/1/3, at 14:23
     */
    @NonNull
    protected long date;

    /**
     * DESC: 性别
     * Created by jinphy, on 2018/1/3, at 14:23
     */
    protected String sex;

    /**
     * DESC: 头像地址
     * Created by jinphy, on 2018/1/3, at 14:24
     */
    protected String avatarUrl;

    /**
     * DESC: 登录状态
     *
     *
     * Created by jinphy, on 2018/1/3, at 14:24
     */
    protected String status;

    /**
     * DESC：验证账号、设备和登录时间的AccessToken，由四部分组成
     *  1、设备id：  IMEI
     *  3、登录时间： 登录时生成(loginTime)，时间由System.currentTimeMillis() 获取
     *  4、登录状态  status
     *  把上面三部分拼接成一个字符串，格式为： IMEI&account&time
     *  然后在经过编码加密生成最终的accessToken，该字段有后台生成，在每次执行登录操作时更新
     *
     * Created by jinphy, on 2018/1/2, at 19:52
     */
    protected String accessToken;


    /**
     * DESC: 一个用户可以有多个好友，所以是一对多关系，即一个User -> 多个Friend
     * Created by jinphy, on 2018/1/3, at 13:32
     */
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
