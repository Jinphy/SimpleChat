package com.example.jinphy.simplechat.modules.show_photo;

import com.example.jinphy.simplechat.models.event_bus.EBBase;
import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/3/24.
 */

public class EBMessage extends EBBase<Message> {

    public final int position;

    public EBMessage(Message data, int position) {
        super(true, data);
        this.position = position;
    }
}
