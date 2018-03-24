package com.example.jinphy.simplechat.services.common_service.aidl.models;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class FileUploader extends WebSocketClient {

    private static int connectTimeout = 60_000;
    
    private Map<String, String> headers;
    private BufferedSource source;

    private FileTask fileTask;
    
    private static String FILE_NAME = "FILE_NAME";
    private static String TASK_TYPE = "TASK_TYPE";

    private OnStart onStart;
    private OnUpdate onUpdate;
    private OnError onError;
    private OnFinish onFinish;


    public static FileUploader with(FileTask task) {
        if (task == null) {
            return null;
        }
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(FILE_NAME, task.getGeneratedFileName());
            headers.put(TASK_TYPE, "upload");
            FileUploader fileUploader = new FileUploader(
                    new URI(task.getUrl()), new Draft_6455(), headers, connectTimeout,task);
            return fileUploader;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private FileUploader(URI uri, Draft draft, Map<String, String> headers, int connectTimeout,FileTask task) {
        super(uri, draft, headers, connectTimeout);
        this.headers = headers;
        this.fileTask = task;
        File file = new File(fileTask.getFilePath());
        if (!file.exists() || file.isDirectory()) {
            return;
        }
        try {
            source = Okio.buffer(Okio.source(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            source = null;
        }
    }


    @Override
    public void onOpen(ServerHandshake handshake) {
        if (source == null) {
            onError(new FileNotFoundException());
            close();
        }
        Map<String, String> map = new HashMap<>();

        int readCount;
        byte[] buffer = new byte[102400];
        // 记录发送的顺序
        int times = 0;
        
        try {
            Thread.sleep(500);
            while ((readCount = source.read(buffer)) != -1) {
                map.put("content", StringUtils.bytesToStr(buffer, 0, readCount));
                map.put("times", (++times)+"");
                this.send(GsonUtils.toJson(map));
                fileTask.addFinishedLength(readCount);
                if (this.onUpdate != null) {
                    this.onUpdate.call(fileTask.getId(), fileTask.getFinishedLength(), fileTask.getTotalFileLength());
                } else {
                }
            }
            map.put("content", "[end]");
            this.send(GsonUtils.toJson(map));
        } catch (Exception e) {
            e.printStackTrace();
            this.onError(e);
        }finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMessage(String message) {
        LogUtils.e(message);
        this.close();
        if ("ok".equals(message)) {
            if (this.onFinish != null) {
                this.onFinish.call(fileTask.getId());
            }
        } else {
            if (this.onError != null) {
                this.onError.call(fileTask.getId());
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        closeSource();
    }

    @Override
    public void onError(Exception ex) {
        closeSource();
        if (this.onError != null) {
            this.onError.call(fileTask.getId());
        }
    }


    private void closeSource() {
        if (source != null) {
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public FileUploader doOnStart(OnStart onStart) {
        this.onStart = onStart;
        return this;
    }

    public FileUploader doOnUpdate(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public FileUploader doOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public FileUploader doOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public FileUploader execute() {
        if (this.onStart != null) {
            this.onStart.call(fileTask.getId());
        }
        this.connect();
        return this;
    }
}
