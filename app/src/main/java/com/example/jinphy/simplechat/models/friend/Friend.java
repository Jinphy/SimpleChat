package com.example.jinphy.simplechat.models.friend;

import com.example.jinphy.simplechat.models.user.User;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import io.reactivex.annotations.NonNull;

/**
 * 好友表
 * Created by jinphy on 2017/8/10.
 */

@Entity
public class Friend {

    public static final String system = "0";

    public static final String status_ok = "ok";

    @Id protected long id;

    /**
     * DESC: 账号
     * Created by jinphy, on 2018/2/27, at 8:49
     */
    @NonNull
    protected String account;

    /**
     * DESC: 昵称
     * Created by jinphy, on 2018/2/27, at 8:48
     */
    protected String name;

    /**
     * DESC: 头像
     * Created by jinphy, on 2018/2/27, at 8:48
     */
    protected String avatar;

    /**
     * DESC: 备注
     * Created by jinphy, on 2018/2/27, at 8:48
     */
    protected String remark;

    /**
     * DESC: 性别
     * Created by jinphy, on 2018/2/27, at 8:48
     */
    protected String sex;

    /**
     * DESC: 地址
     * Created by jinphy, on 2018/2/27, at 8:58
     */
    protected String address;

    /**
     * DESC: 加为好友的日期
     * Created by jinphy, on 2018/2/27, at 8:59
     */
    protected String date;

    /**
     * DESC: 当前好友属于谁的，该字段是一个账号，代表当前好友属于该字段所指的账号的
     * Created by jinphy, on 2018/1/15, at 14:28
     */
    protected String owner;

    /**
     * DESC: 好友状态
     * 取值：
     *  ok：正常好友
     *  waiting：等待验证
     *  blackList:黑名单
     *  deleted：已被删除
     *
     * Created by jinphy, on 2018/1/18, at 12:11
     */
    protected String status;

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

    public void setUser(User user) {
        this.user.setTarget(user);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
