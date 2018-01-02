package com.example.jinphy.simplechat.model.event_bus;

import android.app.Activity;

/**
 * 销毁activity的EventBus的消息类
 * Created by jinphy on 2017/11/18.
 */

public class EBFinishActivityMsg {
    // 销毁所有的activity
    public static final Class<?extends Activity> ALL = Activity.class;

    // 要销毁的activity
    public Class<? extends Activity> which;

    public EBFinishActivityMsg(){
        this.which = ALL;
    }

    public EBFinishActivityMsg(Class<? extends Activity> which) {
        this.which = which;
    }

}