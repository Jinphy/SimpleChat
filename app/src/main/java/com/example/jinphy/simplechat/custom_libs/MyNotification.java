package com.example.jinphy.simplechat.custom_libs;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.DeviceUtils;

import java.util.HashMap;
import java.util.Map;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_NOTIFICATION_CANCEL;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_NOTIFICATION_CLICK;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.getIntent;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class MyNotification {

    private int msgCount;

    public static final int REQUEST_CODE_CLICK = 0;
    public static final int REQUEST_CODE_CANCEL = 1;

    Map<String, Boolean> acccounts = new HashMap<>();

    private MyNotification() {

    }

    private static class InstanceHolder{
        static MyNotification DEFAULT = new MyNotification();
    }

    public static MyNotification getInstance() {
        return InstanceHolder.DEFAULT;
    }

    public synchronized int getMsgCount() {
        return ++msgCount;
    }

    public void notify(String with, String content) {
        long[] vibratePattern = {0, 200, 100, 150};
        DeviceUtils.vibrate(vibratePattern, -1);
        if (!App.atForeground()) {
            DeviceUtils.playRing(R.raw.ring_notification);
            // 如果APP不在前台是则需要通知到状态栏中
            acccounts.put(with, true);
            int withCount = acccounts.size();
            int msgCount = getMsgCount();

            String title;
            if (msgCount > 1) {
                title = App.app().getString(R.string.app_name);
                if (withCount > 1) {
                    content = String.format("%d个好友给您发了%d条消息", withCount, msgCount);
                } else {
                    content = String.format("您有%d条未读消息", msgCount);
                }
            } else {
                User user = UserRepository.getInstance().currentUser();
                if (with.contains("G")) {
                    title = GroupRepository.getInstance().get(with, user.getAccount()).getName();
                } else {
                    title = FriendRepository.getInstance().get(user.getAccount(), with)
                            .getShowName();
                }
            }
            Notification notification = new NotificationCompat.Builder(App.app(), App.app()
                    .getPackageName())
                    .setTicker(content)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setGroup("简聊")
                    .setNumber(msgCount)
                    .setContentIntent(createClickIntent(with, withCount))
                    .setDeleteIntent(createCancelIntent())
                    .build();

            NotificationManagerCompat.from(App.app()).notify(0, notification);
        } else {
            reset();
            NotificationManagerCompat.from(App.app()).cancel(0);
        }
    }

    public void reset() {
        this.msgCount = 0;
        this.acccounts.clear();
    }

    private PendingIntent createClickIntent(String with, int withCount) {
        String msg = with + ":" + withCount;
        Intent click = getIntent(TAG_NOTIFICATION_CLICK, msg);
        return PendingIntent.getBroadcast(
                App.app(),
                REQUEST_CODE_CLICK,
                click,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createCancelIntent() {
        Intent cancel = getIntent(TAG_NOTIFICATION_CANCEL, null);
        return PendingIntent.getBroadcast(
                App.app(),
                REQUEST_CODE_CANCEL,
                cancel,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
