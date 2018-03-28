package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class EBFileTask extends EBBase<String> {

    /**
     * DESC: 进度的百分比
     * Created by jinphy, on 2018/3/28, at 17:54
     */
    public int percent;

    /**
     * DESC: 消息的id
     * Created by jinphy, on 2018/3/28, at 17:55
     */
    public long msgId;


    public EBFileTask( String data) {
        super(true, data);
    }
}
