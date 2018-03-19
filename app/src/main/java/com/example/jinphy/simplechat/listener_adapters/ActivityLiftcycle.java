package com.example.jinphy.simplechat.listener_adapters;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * DESC:
 * Created by jinphy on 2018/1/6.
 */

public interface ActivityLiftcycle  extends Application.ActivityLifecycleCallbacks{

    @Override
    default void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    default void onActivityStarted(Activity activity) {

    }

    @Override
    default void onActivityPaused(Activity activity) {

    }

    @Override
    default void onActivityStopped(Activity activity) {

    }

    @Override
    default void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    default void onActivityDestroyed(Activity activity) {

    }
}
