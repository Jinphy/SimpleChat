package com.example.jinphy.simplechat.models.api.file_transfer.download;

import com.example.jinphy.simplechat.models.api.file_transfer.Result;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public class DownloadSubscriber implements Observer<Result<DownloadTask>> {



    private static class InstanceHolder{
        static final DownloadSubscriber DEFUALT = new DownloadSubscriber();
    }

    public static DownloadSubscriber getInstance() {
        return InstanceHolder.DEFUALT;
    }


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Result<DownloadTask> result) {
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
