package com.example.jinphy.simplechat.api;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jinphy on 2017/11/6.
 */

public class MyWebSocketClient extends WebSocketClient implements Serializable{

    private URI uri;
    private OnOpen onOpen;
    private OnMessage onMessage;
    private OnClose onClose;
    private OnError onError;

    public MyWebSocketClient doOnOpen(OnOpen onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    public MyWebSocketClient doOnMessage(OnMessage onMessage) {
        this.onMessage = onMessage;
        return this;
    }

    public MyWebSocketClient doOnClose(OnClose onClose) {
        this.onClose = onClose;
        return this;
    }

    public MyWebSocketClient doOnError(OnError onError) {
        this.onError = onError;
        return this;
    }


    public static MyWebSocketClient newInstance(String uri) throws URISyntaxException{

        return new MyWebSocketClient(new URI(uri));
    }

    //------------------------------------------------------------------


    private MyWebSocketClient(URI url)  {
        super(url);
    }




    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (onOpen != null) {
            onOpen.onOpen(handshakedata);
        }
    }

    @Override
    public void onMessage(String message) {
        if (onMessage != null) {
            onMessage.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (onClose != null) {
            onClose.onClose(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        if (onError != null) {
            onError.onError(ex);
        }
    }


    interface OnOpen extends Serializable{
        void onOpen(ServerHandshake handShakeData);
    }

    interface OnMessage extends Serializable{
        void onMessage(String message);
    }

    interface OnClose extends  Serializable{
        void onClose(int code, String reason, boolean remote);
    }

    interface OnError extends Serializable{
        public void onError(Exception ex);
    }
}
