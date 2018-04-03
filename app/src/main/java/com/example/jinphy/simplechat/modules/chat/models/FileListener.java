package com.example.jinphy.simplechat.modules.chat.models;

/**
 * DESC:
 * Created by jinphy on 2018/3/22.
 */

public interface FileListener {


    void onStart(long fileTaskId);

    void onUpdate(long fileTaskId,long finishedLength,long totalLength);

    void onError(long fileTaskId);

    void onFinish(long fileTaskId);

}
