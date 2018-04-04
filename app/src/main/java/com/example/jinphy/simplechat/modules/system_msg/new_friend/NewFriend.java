package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.StringUtils;

/**
 * DESC:
 * Created by jinphy on 2018/4/2.
 */

public class NewFriend{
    public Friend friend;
    public Message message;

    public static NewFriend create(Friend friend, Message message) {
        NewFriend newFriend = new NewFriend();
        newFriend.friend = friend;
        newFriend.message = message;
        return newFriend;
    }

    public String getName() {
        return friend.getShowName();
    }

    public String getAccount() {
        return friend.getAccount();
    }

    public String getTime() {
        return StringUtils.formatTime(message.getCreateTime());
    }

    public String getMsg() {
        return message.getContent();
    }

    public boolean isNewMsg() {
        return message.isNew();
    }


}
