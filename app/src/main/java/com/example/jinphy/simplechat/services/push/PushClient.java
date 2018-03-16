package com.example.jinphy.simplechat.services.push;

import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * DESC: 接收服务器推送的客户端
 * Created by jinphy on 2018/1/16.
 */

public class PushClient extends WebSocketClient {

    private static int connectTimeout = 60_000;

    private static String baseUrl = Api.BASE_URL;
    private static String port = Api.PUSH_PORT;
    private static String path = "/push";
    private String url;
    private WeakReference<PushService> service;

    private boolean forceClose = false;

    private boolean trying = false;

    private static final String TAG = "PushClient";

    public static PushClient start(PushService service) {
        User user = UserRepository.getInstance().currentUser();
        if (user == null) {
            return null;
        }
        String params = EncryptUtils.encodeThenEncrypt("account=" + user.getAccount()) ;
        String url = StringUtils.generateURI(
                baseUrl,
                port,
                path,
                params);
        Map<String, String> headers = new HashMap<>();
        headers.put(User_.account.name, user.getAccount());
        headers.put(User_.accessToken.name, user.getAccessToken());
        // TODO: 2018/1/16 设置其他请求头

        try {
            PushClient pushClient = new PushClient(url, headers);
            pushClient.service = new WeakReference<>(service);
            pushClient.connect();
            Log.e(TAG, "start: connect");
            return pushClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    public PushClient(String url,Map<String, String> headers) throws URISyntaxException {
        super(new URI(url), new Draft_6455(), headers, connectTimeout);
        this.url = url;
    }

    @Override
    public void connect() {
        this.trying = true;
        super.connect();
    }

    public void forceClose() {
        forceClose = true;
        close();
    }

    public boolean isTrying() {
        return trying;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        trying = false;
    }

    @Override
    public void onMessage(String message) {
        LogUtils.e("PushClient: " + message);
        if (ObjectHelper.reference(service)) {
            service.get().handleMsg(EncryptUtils.decryptThenDecode(message));
        } else {
            PushService.start(App.app(), PushService.FLAG_INIT);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        trying = false;
        if (!forceClose && ObjectHelper.reference(service)) {
            service.get().onPushInvalidate();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Exception ex) {
        trying = false;
        if (ObjectHelper.reference(service)) {
            service.get().onPushInvalidate();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
