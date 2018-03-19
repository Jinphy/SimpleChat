package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.friend.Friend;

/**
 * DESC:
 * Created by jinphy on 2018/3/3.
 */

public class EBUpdateFriend extends EBBase<Friend> {

    public EBUpdateFriend() {
        super(true, null);
    }
}
