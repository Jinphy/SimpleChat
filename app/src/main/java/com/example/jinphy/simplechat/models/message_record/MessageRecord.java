package com.example.jinphy.simplechat.models.message_record;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * DESC: 消息记录类
 * Created by jinphy on 2017/8/10.
 */

@Entity
public class MessageRecord {

    @Id
    private long id;

    private String owner;

    private ToOne<Friend> with;

    private ToOne<Message> lastMsg;

    /**
     * DESC: 标志是否置顶
     *  两个值：
     *      1、toTop=0，默认值，不置顶
     *      2、toTop=1，置顶
     *
     * Created by jinphy, on 2018/1/18, at 11:05
     */
    private int toTop = 0;

    /**
     * DESC: 未读消息数
     * Created by jinphy, on 2018/1/18, at 10:34
     */
    private int newMsgCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ToOne<Friend> getWith() {
        return with;
    }
    public Friend getFriend() {
        return with.getTarget();
    }

    public void setWith(ToOne<Friend> with) {
        this.with = with;
    }

    public void setWith(Friend with) {
        this.with.setTarget(with);
    }

    public ToOne<Message> getLastMsg() {
        return lastMsg;
    }

    public Message getMessage() {
        return lastMsg.getTarget();
    }


    public void setLastMsg(ToOne<Message> lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setLastMsg(Message lastMsg) {
        this.lastMsg.setTarget(lastMsg);
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }

    public int getToTop() {
        return toTop;
    }

    public void setToTop(int toTop) {
        if (toTop < 0 || toTop > 1) {
            toTop = 0;
        }
        this.toTop = toTop;
    }
}
