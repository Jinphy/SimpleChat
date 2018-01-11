package com.example.jinphy.simplechat.models.event_bus;

import android.content.Intent;

/**
 * DESC:
 * Created by jinphy on 2018/1/10.
 */

public class EBIntent extends EBBase<Intent> {

    public int requestCode;

    private EBIntent(int requestCode, Intent data) {
        super(true, data);
        this.requestCode = requestCode;
    }
    private EBIntent(){
        super(false, null);
    }

    /**
     * DESC: 成功的信息
     * Created by jinphy, on 2018/1/10, at 17:54
     */
    public static EBIntent ok(int requestCode, Intent data) {
        return new EBIntent(requestCode, data);
    }

    /**
     * DESC: 错误的信息
     * Created by jinphy, on 2018/1/10, at 17:55
     */
    public static EBIntent error(){
        return new EBIntent();
    }

}
