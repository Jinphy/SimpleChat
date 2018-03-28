package com.example.jinphy.simplechat.services.common_service.aidl.models;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okio.BufferedSink;
import okio.Okio;

/**
 * DESC:
 * Created by jinphy on 2018/3/22.
 */

public class FileDownloader extends WebSocketClient {

    private static int connectTimeout = 60_000;

    private Map<String, String> headers;
    private BufferedSink sink;

    private FileTask fileTask;

    private static String FILE_NAME = "FILE_NAME";
    private static String TASK_TYPE = "TASK_TYPE";

    private OnStart onStart;
    private OnUpdate onUpdate;
    private OnError onError;
    private OnFinish onFinish;

    /**
     * DESC: 保存从服务器中发送过来的缓存数据
     * Created by jinphy, on 2018/3/22, at 13:59
     */
    private Map<Integer, byte[]> bufferMap = new ConcurrentHashMap<>();

    /**
     * DESC: 服务器发送的次数
     * Created by jinphy, on 2018/3/22, at 13:59
     */
    private int totalTimes;

    /**
     * DESC: 本地将服务器发来的数据写入文件的次数
     * Created by jinphy, on 2018/3/22, at 14:00
     */
    private int writeTimes;


    private static Type type = new TypeToken<Map<String, String>>() {
    }.getType();


    /**
     * DESC: 缓存服务器发来的数据
     * Created by jinphy, on 2018/3/22, at 14:02
     */
    public void putBuffer(int times, byte[] buffer) {
        bufferMap.put(times, buffer);
    }


    /**
     * DESC: 读取缓存数据
     * Created by jinphy, on 2018/3/22, at 14:02
     */
    public synchronized byte[] getBuffer() {
        if (totalTimes == 0 || writeTimes < totalTimes) {
            writeTimes++;
            byte[] buffer;
            while ((buffer = bufferMap.remove(writeTimes)) == null) {
                if (isFinished()) {
                    break;
                }
                continue;
            }
            return buffer;
        } else {
            return null;
        }
    }

    /**
     * DESC: 是否发送完成
     * Created by jinphy, on 2018/3/22, at 14:02
     */
    public boolean isFinished() {
        return totalTimes > 0 && writeTimes >= totalTimes;
    }




    public static FileDownloader with(FileTask task) {
        if (task == null) {
            return null;
        }
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(FILE_NAME, task.getGeneratedFileName());
            headers.put(TASK_TYPE, "downloadPhoto");
            FileDownloader fileDownloader = new FileDownloader(
                    new URI(task.getUrl()), new Draft_6455(), headers, connectTimeout,task);
            return fileDownloader;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private FileDownloader(URI uri, Draft draft, Map<String, String> headers, int connectTimeout, FileTask task) {
        super(uri, draft, headers, connectTimeout);
        this.headers = headers;
        this.fileTask = task;
        File file = new File(fileTask.getFilePath());
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            sink = Okio.buffer(Okio.sink(file));
        } catch (Exception e) {
            e.printStackTrace();
            sink = null;
        }
    }


    @Override
    public void onOpen(ServerHandshake handshake) {
        ThreadPoolUtils.threadPool.execute(()->{
            if (sink == null) {
                // 文件创建失败
                send("error");
                if (this.onError != null) {
                    this.onError.call(fileTask.getId());
                }
                return;
            }
            byte[] buffer;
            try {
                while ((buffer = getBuffer()) != null) {
                    sink.write(buffer);
                    fileTask.addFinishedLength(buffer.length);
                    if (onUpdate != null) {
                        onUpdate.call(fileTask.getId(), fileTask.getFinishedLength(), fileTask.getTotalFileLength());
                    }
                    if (isFinished()) {
                        break;
                    }
                }
                // 上传完毕
                sink.flush();
                sink.close();
                this.close();
                if (onFinish != null) {
                    onFinish.call(fileTask.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (onError != null) {
                    onError.call(fileTask.getId());
                }
            }

        });
    }

    @Override
    public void onMessage(String message) {
        Map<String, String> map = GsonUtils.toBean(message, type);
        String content = map.get("content");
        Integer times = Integer.valueOf(map.get("times"));
        if ("[end]".equals(content)) {
            totalTimes = times;
        } else {
            putBuffer(times, StringUtils.strToBytes(content));
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        closeSink();
    }

    @Override
    public void onError(Exception ex) {
        closeSink();
        if (this.onError != null) {
            this.onError.call(fileTask.getId());
        }
    }


    private void closeSink() {
        if (sink != null) {
            try {
                sink.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public FileDownloader doOnStart(OnStart onStart) {
        this.onStart = onStart;
        return this;
    }

    public FileDownloader doOnUpdate(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public FileDownloader doOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public FileDownloader doOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public FileDownloader execute() {
        if (this.onStart != null) {
            this.onStart.call(fileTask.getId());
        }
        this.connect();
        return this;
    }
}
