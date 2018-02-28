package com.example.jinphy.simplechat.models.api.file_transfer.upload;

import android.text.TextUtils;

import com.example.jinphy.simplechat.models.api.file_transfer.OnProgress;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.io.File;
import java.io.FileNotFoundException;

import okio.BufferedSource;
import okio.Okio;

/**
 * DESC: 文件上传任务
 * Created by jinphy on 2018/1/19.
 */

public class UploadTask {

    private final static Uploader uploader = Uploader.getInstance();


    /**
     * DESC: 待上传的文件的绝对路径
     * Created by jinphy, on 2018/1/19, at 13:32
     */
    String filePath;

    /**
     * DESC: 任务id，唯一标识单个上传任务
     * Created by jinphy, on 2018/1/19, at 13:08
     */
    String taskId;

    /**
     * DESC: 文件大小，单位byte
     * Created by jinphy, on 2018/1/19, at 13:02
     */
    long fileLength;

    /**
     * DESC: 要上传的文件名，改文件名是用来保存在服务器中的文件的文件名，必传
     * Created by jinphy, on 2018/1/19, at 13:09
     */
    String fileName;

    /**
     * DESC: 上传开始时回调，注意这里不是在加入任务队列中就回调，而是在正确执行上传前回调
     * Created by jinphy, on 2018/1/19, at 13:11
     */
    Runnable onStart;

    /**
     * DESC: 回调更新进度
     * Created by jinphy, on 2018/1/19, at 13:22
     */
    OnProgress onProgress;

    /**
     * DESC: 上传时回调，在调用了{@link Uploader#newTask()}方法后要设置该回调手动上传
     * Created by jinphy, on 2018/1/19, at 13:40
     */
    OnUpload onUpload;

    UploadTask(String filePath) {
        this.filePath = filePath;
        this.init();
    }

    UploadTask() {
        this.init();
    }

    private void init() {
        String s = DeviceUtils.deviceId() + System.currentTimeMillis();
        taskId = EncryptUtils.md5(s);
    }

    public BufferedSource source() {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        try {
            File file = new File(filePath);
            this.fileName = file.getName();
            this.fileLength = file.length();
            return Okio.buffer(Okio.source(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UploadTask fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public UploadTask fileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public UploadTask whenStart(Runnable onStart) {
        this.onStart = onStart;
        return this;
    }

    /**
     * DESC: 更新进度是回调
     * Created by jinphy, on 2018/1/19, at 13:17
     */
    public UploadTask whenUpdate(OnProgress update) {
        onProgress = update;
        return this;
    }

    public UploadTask whenUpload(OnUpload onUpload) {
        this.onUpload = onUpload;
        return this;
    }

    /**
     * DESC: 提交上传任务
     * Created by jinphy, on 2018/1/19, at 13:26
     */
    public void submit() {
        if (TextUtils.isEmpty(filePath)) {
            if (fileLength <= 0
                    || TextUtils.isEmpty(fileName)
                    || onUpload == null) {
                ObjectHelper.throwRuntime("fileLength、fileName、onUpload cannot be null when " +
                        "filePath is null");
            }
        }
        uploader.submit(this);
    }
}
