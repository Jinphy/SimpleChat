package com.example.jinphy.simplechat.models.api.send;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBSendError;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC: 消息发送器
 * Created by jinphy on 2018/1/18.
 */

public class Sender extends WebSocketClient implements ObservableOnSubscribe<SendResult<SendTask>>{

    private static final String path = "/send";
    private static final int connectTimeout = 60_000;


    Map<String, SendTask> taskMap;
    private Type resultType;
    private ObservableEmitter<SendResult<SendTask>> emitter;

    private static Sender sender;
    private Disposable disposable;

    private boolean isShutdown = false;

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    /**
     * DESC: 消息发送回执
     *
     *  sendId:发送id
     *  code:  状态码,200 表示成功，100 表示失败
     *
     * Created by jinphy, on 2018/1/18, at 15:11
     */
    @Override
    public void onMessage(String message) {
        message = EncryptUtils.decryptThenDecode(message);
        Map<String, String> map = GsonUtils.toBean(message, resultType);
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onNext(new SendResult<>(map.get("code"),  taskMap.remove(map.get("sendId"))));
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onComplete();
            emitter = null;
        }
        if (!isShutdown) {
            try {
                Thread.sleep(3000);
                EventBus.getDefault().post(new EBSendError());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onError(Exception ex) {
        if (emitter != null && !emitter.isDisposed()) {
            Set<String> keys = taskMap.keySet();
            for (String key : keys) {
                emitter.onNext(new SendResult<>(SendResult.NO, taskMap.remove(key)));
            }
            emitter.onComplete();
            emitter = null;
        }
    }

    /**
     * DESC: 打开消息发送器，在发送消息前调用
     * Created by jinphy, on 2018/1/18, at 14:24
     */
    public void open() {
        if (!this.isOpen()) {
            this.connect();
            Observable.create(this)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable -> this.disposable = disposable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SenderSubscriber.getInstance());
        }
    }

    /**
     * DESC: 关闭消息发射器，在退出聊天界面或者不在需要发送消息是调用
     * Created by jinphy, on 2018/1/18, at 14:24
     */
    public void shutdown(){
        isShutdown = true;
        this.close();
        if (this.disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static class InstanceHolder{
        static Sender DEFAULT = init();
    }

    public static Sender getInstance() {
        if (!InstanceHolder.DEFAULT.isOpen()) {
            InstanceHolder.DEFAULT = init();
        }
        return InstanceHolder.DEFAULT;
    }

    private static Sender init() {
        String url = StringUtils.generateURI(
                Api.BASE_URL,
                Api.SEND_PORT,
                path,
                "");
        Map<String, String> headers = new HashMap<>();
        try {
            return new Sender(url, headers);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Sender(String url, Map<String, String> headers) throws URISyntaxException {
        super(new URI(url), new Draft_6455(), headers, connectTimeout);
        taskMap = new ConcurrentHashMap<>();
        resultType = new TypeToken<Map<String, String>>() {}.getType();
    }

    /**
     * DESC: 创建一个发送消息的任务
     * Created by jinphy, on 2018/1/19, at 9:45
     */
    public static SendTask newTask(Message message) {
        SendTask sendTask = new SendTask();
        sendTask.message = message;
        sendTask.sendId = EncryptUtils.md5(message.getOwner() + message.getWith() + message.getCreateTime());
        return sendTask;
    }

    @Override
    public void subscribe(ObservableEmitter<SendResult<SendTask>> e) throws Exception {
        emitter = e;
    }

    public static boolean isOk() {
        return sender.isOpen();
    }
}
