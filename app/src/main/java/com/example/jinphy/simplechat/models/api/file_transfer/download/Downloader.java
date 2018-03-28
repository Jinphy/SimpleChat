package com.example.jinphy.simplechat.models.api.file_transfer.download;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.file_transfer.Progress;
import com.example.jinphy.simplechat.models.api.file_transfer.Result;
import com.example.jinphy.simplechat.models.api.file_transfer.upload.UploadTask;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okio.BufferedSink;

/**
 * DESC: 文件下载器
 * Created by jinphy on 2018/1/18.
 */

public class Downloader extends WebSocketClient implements ObservableOnSubscribe<Result<DownloadTask>>{


    private static String baseUrl = Api.BASE_URL;
    private static String port = Api.FILE_PORT;
    private static int connectTimeout = 60_000;

    private static ExecutorService threadPool = ThreadPoolUtils.threadPool;

    private Queue<DownloadTask> taskQueue;

    private ObservableEmitter<Result<DownloadTask>> emitter;
    private DownloadTask currentTask;
    BufferedSink sink;

    private Type resultType;

    private static class InstanceHolder{
        static final Downloader DEFAULT = init();
    }

    public static Downloader getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private static Downloader init() {
        String url = StringUtils.generateURI(baseUrl, port, "file", "");
        Map<String, String> headers = new HashMap<>();

        try {
            return new Downloader(url, headers);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Downloader(String url, Map<String,String> headers) throws URISyntaxException {
        super(new URI(url), new Draft_6455(), headers, connectTimeout);
        taskQueue = new PriorityQueue<>();
        resultType = new TypeToken<Map<String, String>>() {
        }.getType();
    }




    @Override
    public void onOpen(ServerHandshake handshake) {
        Observable.create(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DownloadSubscriber.getInstance());
    }

    /**
     * DESC: 接收服务器的返回信息
     *
     *  返回字段：
     *          1、fileLength  文件大小
     *          2、fileName    文件名
     *          3、content     文件内容
     *
     *
     * Created by jinphy, on 2018/1/19, at 16:40
     */
    @Override
    public void onMessage(String message) {
        try {
            message = EncryptUtils.decryptThenDecode(message);
            Map<String, String> map = GsonUtils.toBean(message, resultType);

            // 如果数据正常，则服务器是不传该值的，如果传了则说明服务器异常
            if (map.containsKey("ok")) {
                emitter.onNext(new Result<>(Progress.error(), currentTask));
                sink.flush();
                sink.close();
                currentTask = null;
                execute();
                return;
            }
            if (currentTask.currentLength == 0) {
                currentTask.fileName = map.get("fileName");
                currentTask.fileLength = Long.valueOf(map.get("fileLength"));
                sink = currentTask.sink();
            }
            byte[] bytes = map.get("content").getBytes("utf8");
            sink.write(bytes);
            sink.flush();
            Progress progress = new Progress();
            progress.setCurrent(currentTask.update(bytes.length));
            progress.setTotal(currentTask.fileLength);
            progress.setPercentage(currentTask.percentage());
            emitter.onNext(new Result<>(progress, currentTask));

            if (currentTask.currentLength == currentTask.fileLength) {
                sink.flush();
                sink.close();
                currentTask = null;
                execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onComplete();
        }
    }

    @Override
    public void onError(Exception ex) {
        if (emitter != null && !emitter.isDisposed()) {
            while (!taskQueue.isEmpty()) {
                DownloadTask task = taskQueue.poll();
                emitter.onNext(new Result<>(Progress.error(), task));
            }
            emitter.onError(ex);
        }
    }

    @Override
    public void subscribe(ObservableEmitter<Result<DownloadTask>> e) throws Exception {
        emitter = e;
    }


    public static DownloadTask newTask(String url) {
        return new DownloadTask(url);
    }

    void submit(DownloadTask task) {
        taskQueue.offer(task);
        execute();
    }

    void execute() {
        if (currentTask != null || taskQueue.size() == 0) {
            return;
        }
        synchronized (this) {
            if (taskQueue.size()>0
                    &&emitter != null
                    && !emitter.isDisposed()
                    && currentTask == null) {
                currentTask = taskQueue.poll();
            }
        }

        if (currentTask.onStart!=null) {
            currentTask.onStart.run();
        }

        threadPool.execute(()->{
            // 上传文件的开始信息
            Map<String, Object> map = new HashMap<>();
            map.put("url", currentTask.url);
            map.put("taskId", currentTask.taskId);
            map.put("taskType", "downloadPhoto");
            String start = GsonUtils.toJson(map);
            this.send(EncryptUtils.encodeThenEncrypt(start));
            map.clear();
        });
    }
}
