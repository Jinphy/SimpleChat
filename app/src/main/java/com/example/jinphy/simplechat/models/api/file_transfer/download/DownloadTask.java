package com.example.jinphy.simplechat.models.api.file_transfer.download;

import android.os.Environment;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.api.file_transfer.OnProgress;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.EncryptUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public class DownloadTask {

    private static final Downloader downloader = Downloader.getInstance();

    String url;

    String taskId;

    /**
     * DESC: 要下载的文件的总大小
     * Created by jinphy, on 2018/1/19, at 16:48
     */
    long fileLength;

    /**
     * DESC: 当前已下载的文件大小
     * Created by jinphy, on 2018/1/19, at 16:49
     */
    long currentLength;

    String fileName;

    Runnable onStart;

    OnProgress onProgress;

    DownloadTask(String url) {
        this.url = url;
        String s = DeviceUtils.deviceId() + System.currentTimeMillis();
        taskId = EncryptUtils.md5(s);
    }


    public BufferedSink sink() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        path += "simple_chat/cache/files";

        LogUtils.e(path);
        try {
            File file = new File(path, fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            return Okio.buffer(Okio.sink(file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DownloadTask whenStart(Runnable onStart) {
        this.onStart = onStart;
        return this;
    }

    public DownloadTask whenUpdate(OnProgress update) {
        this.onProgress = update;
        return this;
    }

    /**
     * DESC: 提交下载任务
     * Created by jinphy, on 2018/1/19, at 16:46
     */
    public void submit() {
        downloader.submit(this);
    }

    public long update(long length) {
        currentLength += length;
        return currentLength;
    }

    public float percentage() {
        return (float) (currentLength * 1.0 / fileLength);
    }

}
