package com.example.jinphy.simplechat.models.api.file_transfer.upload;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.file_transfer.Progress;
import com.example.jinphy.simplechat.models.api.file_transfer.Result;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
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
import okio.BufferedSource;

/**
 * DESC: 文件上传器
 * Created by jinphy on 2018/1/18.
 */

public class Uploader extends WebSocketClient implements ObservableOnSubscribe<Result<UploadTask>>{

    private static String baseUrl = Api.BASE_URL;
    private static String port = Api.FILE_PORT;
    private static int connectTimeout = 60_000;

    private static ExecutorService threadPool = ThreadPoolUtils.threadPool;


    Queue<UploadTask> taskQueue;
    private ObservableEmitter<Result<UploadTask>> emitter;
    private UploadTask currentTask;
    BufferedSource source;

    private static class InstanceHolder{
        static final Uploader DEFAULT = init();
    }

    public static Uploader getInstance() {
        return InstanceHolder.DEFAULT;
    }

    /**
     * DESC: 初始化
     * Created by jinphy, on 2018/2/8, at 9:37
     */
    private static Uploader init() {

        // url = ws:196.168.0.1/file
        String url = StringUtils.generateURI(baseUrl, port, "file", "");
        Map<String, String> headers = new HashMap<>();

        try {
            return new Uploader(url, headers);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Uploader(String url, Map<String, String> headers) throws URISyntaxException {
        super(new URI(url), new Draft_6455(), headers, connectTimeout);
        taskQueue = new PriorityQueue<>();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Observable.create(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(UploadSubscriber.getInstance());
    }

    @Override
    public void onMessage(String message) {
        if (emitter != null && !emitter.isDisposed()) {
            Progress progress = GsonUtils.toBean(
                    EncryptUtils.decryptThenDecode(message), Progress.class);
            emitter.onNext(new Result<>(progress,currentTask));
            // 判断是否上传完成
            if (progress.current() == progress.total()) {
                // 上传完成则执行下一个任务
                currentTask = null;
                execute();
            }
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
                UploadTask task = taskQueue.poll();
                emitter.onNext(new Result<>(Progress.error(), task));
            }
            emitter.onError(ex);
        }
    }


    /**
     * DESC: 打开上传器
     * Created by jinphy, on 2018/1/18, at 14:24
     */
    public void open() {
        if (!this.isOpen()) {
            this.connect();
        }
    }

    /**
     * DESC: 关闭上传器
     * Created by jinphy, on 2018/1/18, at 14:24
     */
    public void shutdown(){
        this.close();
    }


    @Override
    public void subscribe(ObservableEmitter<Result<UploadTask>> e) throws Exception {
        emitter = e;
    }

    /**
     * DESC: 根据指定的待上传的文件的绝对路径创建新的上传任务
     * Created by jinphy, on 2018/1/19, at 12:58
     */
    public static UploadTask newTask(String filePath) {
        return new UploadTask(filePath);
    }

    /**
     * DESC: 创建一个新任务，使用该方法创建的新任务在上传时手动上传具体内容
     * Created by jinphy, on 2018/1/19, at 12:59
     */
    public static UploadTask newTask() {
        return new UploadTask();
    }

    /**
     * DESC: 提交上传任务
     * Created by jinphy, on 2018/1/19, at 15:37
     */
    void submit(UploadTask task) {
        taskQueue.offer(task);
        execute();
    }

    /**
     * DESC: 执行上传任务
     * Created by jinphy, on 2018/1/19, at 14:49
     */
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
            source = currentTask.source();
            // 上传文件的开始信息
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", currentTask.fileName);
            map.put("fileLength", currentTask.fileLength);
            map.put("taskId", currentTask.taskId);
            map.put("status", "start");
            map.put("taskType", "upload");
            String start = GsonUtils.toJson(map);
            this.send(EncryptUtils.encodeThenEncrypt(start));
            map.clear();

            int byteCount = 102400; // 100KB
            byte[] buffer = new byte[byteCount];

            // 上传文件体
            if (source != null) {
                try {
                    map.put("taskId", currentTask.taskId);
                    while (source.read(buffer) != -1) {
                        map.put("content", new String(buffer, "utf8"));
                        this.send(EncryptUtils.encodeThenEncrypt(GsonUtils.toJson(map)));
                    }
                    map.remove("content");
                    map.put("status", "end");
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                currentTask.onUpload.call(this, currentTask.taskId);
                map.put("taskId", currentTask.taskId);
                map.put("status", "end");
            }
        });
    }
}
