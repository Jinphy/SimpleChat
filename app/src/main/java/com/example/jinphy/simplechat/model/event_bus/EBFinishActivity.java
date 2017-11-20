package com.example.jinphy.simplechat.model.event_bus;

import android.app.Activity;

/**
 * 销毁activity的EventBus的消息类
 * Created by jinphy on 2017/11/18.
 */

public class EBFinishActivity {
    // 销毁所有的activity
    public static final Class<?extends Activity> ALL = Activity.class;

    // 要销毁的activity
    public Class<? extends Activity> which;

    public EBFinishActivity(){
        this.which = ALL;
    }

    public EBFinishActivity(Class<? extends Activity> which) {
        this.which = which;
    }

}
