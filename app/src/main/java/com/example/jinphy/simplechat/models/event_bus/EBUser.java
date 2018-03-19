package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.user.User;

/**
 * DESC:
 * Created by jinphy on 2018/1/11.
 */

public class EBUser extends EBBase<User> {

    public EBUser(boolean ok, User data) {
        super(ok, data);
    }
}
