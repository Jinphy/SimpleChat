package com.example.jinphy.simplechat.models.message_record;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * DESC: 消息记录类
 * Created by jinphy on 2017/8/10.
 */

@Entity
public class MessageRecord implements Comparable<MessageRecord>{

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

    /**
     * DESC: 倒序排列
     * Created by jinphy, on 2018/3/1, at 19:36
     */
    @Override
    public int compareTo(@NonNull MessageRecord other) {
        if (toTop > other.toTop) {
            return -1;
        } else if (toTop < other.toTop) {
            return 1;
        }
        Long t1 = Long.valueOf(getMessage().getCreateTime());
        Long t2 = Long.valueOf(other.getMessage().getCreateTime());
        if (t1 > t2) {
            return -1;
        } else {
            return 1;
        }
    }

    public static void sort(List<MessageRecord> records) {
        if (records == null || records.size() == 0) {
            return;
        }
        MessageRecord[] temp = new MessageRecord[records.size()];
        records.toArray(temp);
        Arrays.sort(temp);
        records.clear();
        records.addAll(Arrays.asList(temp));
    }
}
