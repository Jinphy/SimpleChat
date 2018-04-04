package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/3/4.
 */

public class EBService extends EBBase<Message> {

    public EBService() {
        super(true, null);
    }


    public EBService(Message msg) {
        super(true, msg);
    }
}
