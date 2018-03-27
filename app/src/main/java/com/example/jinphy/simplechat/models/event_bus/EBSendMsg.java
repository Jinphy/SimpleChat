package com.example.jinphy.simplechat.models.event_bus;

import com.example.jinphy.simplechat.models.api.common.Response;

/**
 * DESC:
 * Created by jinphy on 2018/3/26.
 */

public class EBSendMsg extends EBBase<Long> {

    public static EBSendMsg make(String code, String msgId) {
        return new EBSendMsg(Response.YES.equals(code), Long.valueOf(msgId));
    }


    public EBSendMsg(boolean ok, Long data) {
        super(ok, data);
    }
}
