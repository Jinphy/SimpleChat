package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.friend.Friend;

/**
 * DESC:
 * Created by jinphy on 2018/3/1.
 */

public class EBFriend extends EBBase<Friend> {

    public EBFriend(Friend data) {
        super(true, data);
    }
}
