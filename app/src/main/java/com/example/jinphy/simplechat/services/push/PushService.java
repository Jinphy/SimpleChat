package com.example.jinphy.simplechat.services.push;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.event_bus.EBService;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PushService extends Service {

    public static final String FLAG = "FLAG";
    public static final String FLAG_INIT = "FLAG_INIT";
    public static final String FLAG_CLOSE = "FLAG_CLOSE";

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private PushClient pushClient;
    private PushManager pushManager;
    private Type messageType;


    private boolean started = false;


    /**
     * DESC: 根据指定的命令启动服务，同时可以利用该命令来控制该服务
     *
     *
     * Created by jinphy, on 2018/1/16, at 13:40
     */
    public static void start(Context context,String command) {
        Intent intent = new Intent(context, PushService.class);
        intent.putExtra(FLAG, command);
        context.startService(intent);
    }

    private static final String TAG = "PushService";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 当第一次启动时，初始化
        if (intent != null) {
            switch (intent.getStringExtra(FLAG)) {
                case FLAG_INIT:
                    if (started) {
                        return START_REDELIVER_INTENT;
                    }
                    started = true;
                    init();
                    EventBus.getDefault().register(this);
                    // 返回该值，当进程被杀死后会自动重启，并且会重新传递intent
                    return START_REDELIVER_INTENT;
                case FLAG_CLOSE:
                    if (!started) {
                        return START_NOT_STICKY;
                    }
                    started = false;
                    if (pushClient != null && !pushClient.isClosed()) {
                        pushClient.close();
                        pushManager = null;
                    }
                    EventBus.getDefault().unregister(this);
                    stopSelf();
                    return START_NOT_STICKY;
            }
        }

        return START_REDELIVER_INTENT;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // no-op
        return null;
    }


    /**
     * DESC: 初始化
     * Created by jinphy, on 2018/1/16, at 13:35
     */
    private synchronized void init() {
        ThreadPoolUtils.threadPool.execute(()->{
            if (pushClient != null && !pushClient.isClosed()) {
                pushClient.close();
            }
            pushManager = PushManager.getInstance(this);
            pushClient = PushClient.start(this);
            messageType = new TypeToken<List<Map<String, String>>>() {}.getType();
        });
    }
    public User getUser() {
        return pushManager.getUser();
    }

    /**
     * DESC: 处理从服务器中推送过来的消息
     *
     * @param msgs json数组，是一个消息列表
     * Created by jinphy, on 2018/1/16, at 13:01
     */
    public void handleMsg(String msgs) {
        threadPool.execute(()->{
            List<Map<String, String>> messages = GsonUtils.toBean(msgs, messageType);
            pushManager.handleMessage(messages);
        });
    }

    public void onPushError() {
        init();
    }

    /**
     * DESC: 通知主进程更新界面
     * Created by jinphy, on 2018/3/4, at 12:48
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EBService msg) {
        // 通知更新界面
        AppBroadcastReceiver.send(this, AppBroadcastReceiver.MESSAGE);
    }
}
