package com.example.jinphy.simplechat.models.api.file_transfer.upload;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public interface OnUpload {

    void call(Uploader uploader, String taskId);
}
