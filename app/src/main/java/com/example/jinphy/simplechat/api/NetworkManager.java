package com.example.jinphy.simplechat.api;

import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.reactivex.annotations.NonNull;


/**
 * Created by jinphy on 2017/11/6.
 */

public class NetworkManager {

    // 宿舍WiFi
//    public static final String HOST = "ws://192.168.0.3";
//    成和WiFi
    public static final String HOST = "ws://192.168.3.21";
//    我的手机WiFi
//    public static final String HOST = "ws://192.168.43.224";
    public static final String PUSH_PORT = "4540";
    public static final String SEND_PORT = "4541";
    public static final String COMMON_PORT = "4542";
    public static final String sendMsgClientURI =
            StringUtils.generateURI(HOST, SEND_PORT, "/send", null);

    private static NetworkManager instance;
    private MyWebSocketClient sendMsgClient;// 聊天室发送信息的专用客户端

    private NetworkManager() {
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }


    /**
     * 注册发送消息的客户端连接，在进入聊天界面时注册
     *
     * */
    public void registerSendMSgClient() {
        if (sendMsgClient == null) {
            try {
                sendMsgClient = MyWebSocketClient.newInstance(sendMsgClientURI)
                        .doOnOpen(this::onOpen)
                        .doOnMessage(this::onMessage)
                        .doOnClose(this::onClose)
                        .doOnError(this::onError);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (!sendMsgClient.isConnecting()) {
            sendMsgClient.connect();
        }
    }

    /**
     * 注销发送聊天消息的客户端连接，离开聊天界面时注销
     *
     *
     * */
    public void unregisterSendMsgClient() {
        if (sendMsgClient != null && sendMsgClient.isConnecting()) {
            sendMsgClient.close();
            sendMsgClient = null;
        }
    }

    /**
     * 向服务器查找给定账号的用户是否存在，如果存在则返回字符串"yes",
     * 如果不存在则返回字符串"no"，如果出现异常则返回异常
     */
    public void findUser(String account, @NonNull Consumer callback) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("account", account);
            String findUserURI =
                    StringUtils.generateURI(HOST, COMMON_PORT, "/user/findUser", params);
            MyWebSocketClient client = MyWebSocketClient.newInstance(findUserURI);
            client.doOnMessage(message -> {
                callback.accept(new Response(message));
                client.close();
            })
                    .doOnError(ex -> {
                        callback.accept(new Response(ex));
                        client.close();
                    })
                    .connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器中创建新用户，如果创建成功则返回字符串"yes",如果不成功则返回 字符串"no"
     * 否则返回错误
     */
    public void createNewUser(String account, String password, String date,Consumer callback) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("account", account);
            params.put("password", password);
            params.put("date", date);
            String createNewUserUri = StringUtils
                    .generateURI(HOST, COMMON_PORT, "/user/createNewUser", params);

            MyWebSocketClient client = MyWebSocketClient.newInstance(createNewUserUri);
            client.doOnMessage(message -> {
                callback.accept(new Response(message));
                client.close();
            })
                    .doOnError(ex -> {
                        ex.printStackTrace();
                        callback.accept(new Response(ex));
                        client.close();
                    })
                    .connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "NetworkManager";
    public void login(String account, String password,String deviceId, Consumer callback) {
        BaseApplication.e(TAG, "login: password = "+password);
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("account", account);
            params.put("password", password);
            params.put("deviceId", deviceId);
            String createNewUserUri = StringUtils
                    .generateURI(HOST, COMMON_PORT, "/user/login", params);
            MyWebSocketClient client = MyWebSocketClient.newInstance(createNewUserUri);
            client.doOnMessage(message -> {
                BaseApplication.e(TAG, "login: message = "+message);
                callback.accept(new Response(message));
                client.close();
            })
                    .doOnError(ex -> {
                        ex.printStackTrace();
                        callback.accept(new Response(ex));
                        client.close();
                    })
                    .connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //-----------------------请求服务器回调函数-------------------------
    //================================================================

    public void onOpen(ServerHandshake handshakedata) {

    }

    public void onMessage(String message) {
    }

    public void onClose(int code, String reason, boolean remote) {
    }

    public void onError(Exception ex) {

    }


}
