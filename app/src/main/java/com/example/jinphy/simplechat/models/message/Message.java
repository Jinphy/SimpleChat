package com.example.jinphy.simplechat.models.message;

import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.friend.Friend;

import java.util.List;
import java.util.Map;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * DESC: 消息类
 * Created by jinphy on 2017/8/13.
 */

@Entity
public class Message implements Comparable<Message>{

    /**
     * DESC: 消息的来源类型：发送的消息
     * Created by jinphy, on 2018/1/16, at 16:42
     */
    public static final int SEND = 0;

    /**
     * DESC: 消息的来源类型：接受的消息
     * Created by jinphy, on 2018/1/16, at 16:43
     */
    public static final int RECEIVE = 1;


    /**
     * DESC: 系统消息
     * Created by jinphy, on 2018/1/16, at 15:24
     */
    public static final String TYPE_SYSTEM = "system";

    /**
     * DESC: 添加好友消息
     * Created by jinphy, on 2018/1/16, at 15:24
     */
    public static final String TYPE_FRIEND = "friend";

    /**
     * DESC: 聊天的文本消息
     * Created by jinphy, on 2018/1/16, at 15:24
     */
    public static final String TYPE_CHAT_TEXT = "text";

    /**
     * DESC: 聊天的图片消息
     * Created by jinphy, on 2018/1/16, at 15:25
     */
    public static final String TYPE_CHAT_IMAGE = "image";

    /**
     * DESC: 聊天的语音消息
     * Created by jinphy, on 2018/1/16, at 16:49
     */
    public static final String TYPE_CHAT_VOICE = "voice";

    public static final String TYPE_CHAT_VIDEO = "video";

    /**
     * DESC: 系统消息，账号过期
     * Created by jinphy, on 2018/3/2, at 8:56
     */
    public static final String TYPE_SYSTEM_ACCOUNT_INVALIDATE = "system_account_invalidate";

    /**
     * DESC: 系统消息，添加新好友
     * Created by jinphy, on 2018/3/2, at 8:57
     */
    public static final String TYPE_SYSTEM_ADD_FRIEND = "system_add_friend";

    /**
     * DESC: 系统消息，同意添加好友
     * Created by jinphy, on 2018/3/2, at 15:52
     */
    public static final String TYPE_SYSTEM_ADD_FRIEND_AGREE = "system_add_friend_agree";

    /**
     * DESC: 系统消息，重新加载指定的好友
     * Created by jinphy, on 2018/3/2, at 19:58
     */
    public static final String TYPE_SYSTEM_RELOAD_FRIEND = "system_reload_friend";

    /**
     * DESC: 系统消息，删除好友
     * Created by jinphy, on 2018/3/2, at 20:49
     */
    public static final String TYPE_SYSTEM_DELETE_FRIEND = "system_delete_friend";

    /**
     * DESC: 系统消息，公告
     * Created by jinphy, on 2018/3/2, at 12:54
     */
    public static final String TYPE_SYSTEM_NOTICE = "system_notice";

    /**
     * DESC: 聊天的文件消息
     * Created by jinphy, on 2018/1/16, at 16:50
     */
    public static final String TYPE_CHAT_FILE = "file";

    public static final String FROM_ACCOUNT = "fromAccount";

    public static final String TO_ACCOUNT = "toAccount";

    @Id
    private long id;

    @NonNull
    private int sourceType;//消息来源类型，有发送和接收两种

    @NonNull
    private String contentType;// 消息内容类型，有文本等

    private String content;//消息的内容，根据contentType来解析具体的消息内容

    @NonNull
    private String createTime;//消息创建时间

    @NonNull
    private boolean isNew = true;// 消息的状态，分为未读消息和已读消息


    private boolean hasSent = true; // 判断是否发送成功，默认是成功的


    /**
     * DESC: 注意服务器中的消息类没有来源类型sourceType，owner和with，只有from 和 to
     * 代表消息从哪个账号发出，发向哪个账号
     *
     * Created by jinphy, on 2018/1/16, at 16:39
     */
    @NonNull
    private String owner; // 消息的拥有者，即该消息是当前owner账号的，owner是个账号

    @NonNull
    private String with; // 消息的参与者，即该消息的参与者，

    private String extra;

    public static Message[] parse(List<Map<String, String>> messageList) {
        if (messageList==null || messageList.size() == 0) {
            return null;
        }
        Message[] result = new Message[messageList.size()];
        int i = 0;
        for (Map<String, String> msg : messageList) {
            result[i++] = parse(msg);
        }
        return result;
    }

    public static Message parse(Map<String, String> msg) {
        Message message = new Message();
        message.setContent(msg.get(Message_.content.name));
        message.setContentType(msg.get(Message_.contentType.name));
        message.setCreateTime(msg.get(Message_.createTime.name));
        message.setSourceType(Message.RECEIVE);
        message.setOwner(msg.get(Message.TO_ACCOUNT));
        message.setWith(msg.get(Message.FROM_ACCOUNT));
        message.setExtra(msg.get(Message_.extra.name));
        return message;
    }




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(@NonNull int sourceType) {
        this.sourceType = sourceType;
    }

    @NonNull
    public String getContentType() {
        return contentType;
    }

    public void setContentType(@NonNull String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(@NonNull String createTime) {
        this.createTime = createTime;
    }

    @NonNull
    public boolean isNew() {
        return isNew;
    }

    public void setNew(@NonNull boolean aNew) {
        isNew = aNew;
    }

    @NonNull
    public String getOwner() {
        return owner;
    }

    public void setOwner(@NonNull String owner) {
        this.owner = owner;
    }

    @NonNull
    public String getWith() {
        return with;
    }

    public void setWith(@NonNull String with) {
        this.with = with;
    }

    public boolean hasSent() {
        return hasSent;
    }

    public void setHasSent(boolean hasSent) {
        this.hasSent = hasSent;
    }

    public boolean getHasSent() {
        return this.hasSent;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * DESC: 过滤消息是否需要保存到数据库
     *
     * Created by jinphy, on 2018/2/27, at 17:12
     */
    public boolean needSave() {
        // "0" 表示系统消息
        if (Friend.system.equals(getWith())) {
            switch (getContentType()) {
                case Message.TYPE_SYSTEM_ACCOUNT_INVALIDATE:
                case Message.TYPE_SYSTEM_ADD_FRIEND_AGREE:
                case Message.TYPE_SYSTEM_RELOAD_FRIEND:
                case Message.TYPE_SYSTEM_DELETE_FRIEND:
                    return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(@NonNull Message other) {
        Long t1 = Long.valueOf(this.createTime);
        Long t2 = Long.valueOf(other.createTime);
        if (t1 > t2) {
            return -1;
        } else {
            return 1;
        }
    }
}