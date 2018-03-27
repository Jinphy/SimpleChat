package com.example.jinphy.simplechat.models.message_record;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.Arrays;
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

    @Id(assignable = true)
    private long id;

    private String owner;

    /**
     * DESC: 聊天对象是一个好友时，则赋该值
     * Created by Jinphy, on 2018/3/6, at 14:32
     */
    private ToOne<Friend> withFriend;

    /**
     * DESC: 聊天对象时一个群时，则赋该值
     * Created by Jinphy, on 2018/3/6, at 14:33
     */
    private ToOne<Group> withGroup;

    /**
     * DESC: 聊天记录的最新消息
     * Created by Jinphy, on 2018/3/6, at 14:33
     */
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



    public ToOne<Friend> getWithFriend() {
        return withFriend;
    }

    public Friend getFriend() {
        return withFriend.getTarget();
    }

    public void setWithFriend(ToOne<Friend> withFriend) {
        this.withFriend = withFriend;
    }

    public void setGroup(Group group) {
        if (group == null) {
            return;
        }
        this.withGroup.setTarget(group);
    }

    public ToOne<Group> getWithGroup() {
        return withGroup;
    }

    public Group getGroup() {
        return withGroup.getTarget();
    }

    public void setWithGroup(ToOne<Group> withGroup) {
        this.withGroup = withGroup;
    }

    public void setWith(Friend with) {
        if (with == null) {
            return;
        }
        this.withFriend.setTarget(with);
    }

    public void setWith(Group with) {
        if (with == null) {
            return;
        }
        this.withGroup.setTarget(with);
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
        if (newMsgCount < 0) {
            this.newMsgCount = 0;
        } else {
            this.newMsgCount = newMsgCount;
        }
    }

    public void addNewMsgCount() {
        setNewMsgCount(getNewMsgCount() + 1);
    }


    public int getToTop() {
        return toTop;
    }


    public boolean isToTop() {
        return toTop == 1;
    }

    /**
     * DESC: 改变是否置顶
     * Created by jinphy, on 2018/3/19, at 19:12
     */
    public void updateToTop() {
        toTop = (++toTop) % 2;
    }

    public void setToTop(int toTop) {
        if (toTop < 0 || toTop > 1) {
            toTop = 0;
        }
        this.toTop = toTop;
    }

    /**
     * DESC: 获取聊天对象的账号，可能是一个好友的账号，也可能是一个群聊的群号
     * Created by Jinphy, on 2018/3/6, at 14:29
     */
    public String getWith() {
        if (withGroup.getTarget() != null) {
            return withGroup.getTarget().getGroupNo();
        }
        if (withFriend.getTarget() != null) {
            return withFriend.getTarget().getAccount();
        }
        return "";
    }


    public String getMsgType() {
        Message message = getMessage();
        if (message == null) {
            return "";
        }
        return message.getContentType();
    }


    public String getName() {
        if (withGroup.getTarget() != null) {
            return withGroup.getTarget().getName();
        } else {
            return withFriend.getTarget().getShowName();
        }
    }


    /**
     * DESC: 获取消息
     * Created by Jinphy, on 2018/3/6, at 14:35
     */
    public String getContent() {
        if (lastMsg.getTarget() != null) {
            return lastMsg.getTarget().getContent();
        }
        return "";
    }

    public String getTime() {
        if (lastMsg.getTarget() != null) {
            return StringUtils.formatDate(Long.valueOf(lastMsg.getTarget().getCreateTime()));
        }
        return "";
    }

    public boolean isSystem() {
        return StringUtils.equal(Friend.system, getWith());
    }

    public boolean isGroup() {
        return getWith().contains("G");
    }

    public String extra(String key) {
        Message message = getMessage();
        if (message == null) {
            return null;
        }
        return message.extra(key);
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

        Message m1 = getMessage();
        Message m2 = other.getMessage();
        if (m1 == null && m2 == null) {
            return 0;
        } else if (m1 == null) {
            return 1;
        } else if (m2 == null) {
            return -1;
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
