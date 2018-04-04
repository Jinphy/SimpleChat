package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/4/4.
 */

public class EBNotificationEvent extends EBBase<String> {


    public static EBNotificationEvent startChatActivity(String withAccount) {
        return new EBNotificationEvent(withAccount);
    }

    public EBNotificationEvent(String data) {
        super(true, data);
    }
}
