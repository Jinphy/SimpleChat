package com.example.jinphy.simplechat.models.api.file_transfer.upload;

import com.example.jinphy.simplechat.models.api.file_transfer.Result;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public class UploadSubscriber implements Observer<Result<UploadTask>> {


    private static class InstanceHolder{
        static UploadSubscriber DEFAULT = new UploadSubscriber();
    }

    public static UploadSubscriber getInstance() {
        return InstanceHolder.DEFAULT;
    }


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Result<UploadTask> result) {
        if (result.task().onProgress != null) {
            result.task().onProgress.call(result.progress());
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
