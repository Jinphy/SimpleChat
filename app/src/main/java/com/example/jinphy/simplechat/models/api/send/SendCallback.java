package com.example.jinphy.simplechat.models.api.send;

import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */
public interface SendCallback {
    void call(SendResult<Message> result);
}
