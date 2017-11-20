package com.example.jinphy.simplechat.model.event_bus;

import android.app.Activity;

/**
 * Created by jinphy on 2017/11/19.
 */

public class EBActivity {

    public Activity activity;
    public boolean resume;

    public EBActivity(){}

    public EBActivity(Activity activity,boolean resume) {
        this.activity = activity;
        this.resume = resume;
    }
}
