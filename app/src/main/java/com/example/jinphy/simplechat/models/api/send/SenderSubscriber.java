package com.example.jinphy.simplechat.models.api.send;

import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

class SenderSubscriber implements Observer<SendResult<SendTask>> {

    MessageRepository messageRepository;

    Disposable disposable;


    private static class InstanceHolder{
        static final SenderSubscriber DEFAULT = new SenderSubscriber();
    }

    public static SenderSubscriber getInstance() {
        return InstanceHolder.DEFAULT;
    }

    public SenderSubscriber() {
        messageRepository = MessageRepository.getInstance();
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(SendResult<SendTask> result) {
        if (SendResult.OK.equals(result.code)) {
            result.data.message.setStatus(Message.STATUS_OK);
        } else {
            result.data.message.setStatus(Message.STATUS_NO);
        }
        ThreadPoolUtils.threadPool.execute(()-> messageRepository.saveReceive(result.data.message));

        if (result.data.onFinal != null) {
            result.data.onFinal.call(new SendResult<>(result.code, result.data.message));
        }
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {

    }
}
