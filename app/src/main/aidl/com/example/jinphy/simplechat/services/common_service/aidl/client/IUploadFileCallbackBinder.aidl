// IUploadFileCallbackBinder.aidl
package com.example.jinphy.simplechat.services.common_service.aidl.client;

// Declare any non-default types here with import statements

interface IUploadFileCallbackBinder {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */


        void onStart(long fileTaskId);

        void onUpdate(long fileTaskId,long finishedLength,long totalLength);

        void onError(long fileTaskId);

        void onFinish(long fileTaskId);

        int getTag();




}
