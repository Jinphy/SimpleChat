package com.example.jinphy.simplechat.models.api.file_transfer;

import com.example.jinphy.simplechat.models.api.file_transfer.upload.UploadTask;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public class Result<T> {

    Progress progress;

    T task;

    public Result(Progress progress, T task) {
        this.progress = progress;
        this.task = task;
    }

    public Progress progress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public T task() {
        return task;
    }

    public void setTask(T task) {
        this.task = task;
    }
}
