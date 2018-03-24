package com.example.jinphy.simplechat.services.common_service.aidl.models;

/**
 * DESC:
 * Created by jinphy on 2018/3/22.
 */

public interface OnUpdate {

    void call(long fileTaskId, long finishedLength, long totalLength);
}
