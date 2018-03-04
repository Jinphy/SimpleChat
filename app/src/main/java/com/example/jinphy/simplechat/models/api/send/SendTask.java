package com.example.jinphy.simplechat.models.api.send;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * DESC: 消息发射任务
 * Created by jinphy on 2018/1/19.
 */
public class SendTask {

    private static ExecutorService threadPool = ThreadPoolUtils.threadPool;

    /**
     * DESC: 消息发送器
     * Created by jinphy, on 2018/1/19, at 9:49
     */
    private Sender sender;

    /**
     * DESC: 发送id，唯一标识单个发送任务
     * Created by jinphy, on 2018/1/19, at 9:49
     */
    String sendId;

    /**
     * DESC: 发送开始时调用
     * Created by jinphy, on 2018/1/19, at 9:50
     */
    transient Runnable onStart;

    /**
     * DESC: 发送结束时调用
     * Created by jinphy, on 2018/1/19, at 9:50
     */
    transient SendCallback onFinal;

    /**
     * DESC: 要发送的消息对象
     * Created by jinphy, on 2018/1/19, at 9:50
     */
    Message message;

    public SendTask() {
        sender = Sender.getInstance();
    }

    /**
     * DESC: 设置消息发送开始时的回调
     * Created by jinphy, on 2018/1/19, at 9:51
     */
    public SendTask whenStart(Runnable onStart) {
        this.onStart = onStart;
        return this;
    }

    /**
     * DESC: 设置消息发送结束时的回调
     * Created by jinphy, on 2018/1/19, at 9:51
     */
    public SendTask whenFinal(SendCallback onFinal) {
        this.onFinal = onFinal;
        return this;
    }

    /**
     * DESC: 执行消息发送
     * Created by jinphy, on 2018/1/19, at 9:46
     */
    public void send() {
        if (message == null) {
            return;
        }
        if (onStart != null) {
            onStart.run();
        }
        threadPool.execute(()->{
            // TODO: 2018/1/18 在发送消息前要先判断消息的类型，如果是图片，视屏等文件的话，要先上传这些文件，然后在发送消息
            sender.taskMap.put(sendId, this);
            Map<String, String> map = new HashMap<>();
            map.put("sendId", sendId);
            map.put("fromAccount", message.getOwner());
            map.put("toAccount", message.getWith());
            map.put("createTime", message.getCreateTime());
            map.put("content", message.getContent());
            map.put("contentType", message.getContentType());

            String body = GsonUtils.toJson(map);
            body = EncryptUtils.encodeThenEncrypt(body);
            if (sender.isOpen()) {
                sender.send(body);
            } else {
                sender.fail(sender.taskMap.remove(sendId));
            }
        });
    }

}
