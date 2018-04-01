package com.example.jinphy.simplechat.modules.chat;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DESC:
 * Created by jinphy on 2018/4/1.
 */

public class MyData {

    public  Group withGroup;

    public Friend withFriend;

    public Member memberOfMe;

    public String with;

    public String owner;

    public ChatAdapter adapter;

    public final boolean isChatWithFriend;


    /**
     * DESC: 缓存正在发送的或者正在下载语音信息的消息
     * Created by jinphy, on 2018/3/26, at 9:29
     */
    public final Map<Long, Message> msgMap = new ConcurrentHashMap<>();

    public static MyData init(String with) {
        return new MyData(with);
    }

    private MyData(String with) {
        if (ObjectHelper.isTrimEmpty(with)) {
            ObjectHelper.throwIlleagalArgs("with account cannot be null");
        }
        this.with = with;
        isChatWithFriend = !with.contains("G");
    }

    public void update(ChatPresenter presenter) {
        this.owner = presenter.getOwner();
        if (isChatWithFriend) {
            withFriend = presenter.getFriend(with);
        } else {
            withGroup = presenter.getGroup(with);
            memberOfMe = presenter.getSelfMember(with);
        }
        adapter = new ChatAdapter(presenter.getUserAvatar(), with);

    }
}
