package com.example.jinphy.simplechat.models.event_bus;

import android.app.Activity;

/**
 * 销毁activity的EventBus的消息类
 * Created by jinphy on 2017/11/18.
 */

public class EBFinishActivityMsg {
    // 销毁所有的activity
    public static final Class<?extends Activity> ALL = Activity.class;

    // 指定的activity
    public Class<? extends Activity> which;

    public boolean kill = true;

    public EBFinishActivityMsg(){
        this.which = ALL;
    }

    public EBFinishActivityMsg(Class<? extends Activity> which) {
        this.which = which;
    }


    public EBFinishActivityMsg(Class<? extends Activity> which, boolean kill) {
        this.which = which;
        this.kill = kill;
    }
}
