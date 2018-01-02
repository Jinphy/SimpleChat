package com.example.jinphy.simplechat.api;

import android.content.Context;

import com.example.jinphy.simplechat.annotations.Get;
import com.example.jinphy.simplechat.annotations.Post;

import org.java_websocket.WebSocket;

/**
 * API工厂类，专治各种不服
 *
 * Created by Jinphy on 2017/12/6.
 */

public abstract class Api {

    // 宿舍WiFi
        public static String BASE_URL = "ws://192.168.0.3";
    //    成和WiFi
    //    public static String BASE_URL = "ws://192.168.3.21";
    //    我的手机WiFi
//        public static String BASE_URL = "ws://192.168.43.224";
    //    公司WiFi
    //    public static String BASE_URL = "ws://172.16.11.134";
    // jysb3 wifi
//    public static String BASE_URL = "ws://192.168.1.200";

    /**
     * DESC: 推送通道端口
     * Created by jinphy, on 2017/12/4, at 21:33
     */
    public static String PUSH_PORT = "4540";

    /**
     * DESC: 发送信息通道端口
     * Created by jinphy, on 2017/12/4, at 21:33
     */
    public static String SEND_PORT = "4541";

    /**
     * DESC: 普通网络请求端口
     * Created by jinphy, on 2017/12/4, at 21:34
     */
    public static String COMMON_PORT = "4542";

    //===================工厂方法==========================================================
    /**
     * DESC: 创建一个通用网络请求API
     * Created by Jinphy, on 2017/12/6, at 13:03
     */
    public static<U> ApiInterface<Response<U>> common(Context context) {
        return CommonApi.create(context);
    }

    /**
     * DESC: 创建一个合并网络请求Api
     * Created by Jinphy, on 2017/12/6, at 13:03
     */
    public static<U> ApiInterface<Response<U>[]> zipper(Context context) {
        return ZipApi.create(context);
    }



    /**
     * DESC: 创建一个短信接口API
     * Created by Jinphy, on 2017/12/6, at 13:05
     */
    public static ApiInterface<Response<String>> sms(Context context) {
        return SMSSDKApi.create(context);
    }


    //===================请求接口==========================================================
    /**
     * DESC: 网络请求路径
     * Created by jinphy, on 2017/12/4, at 21:38
     */
    public interface Path {

        @Post
        String login = "/user/login";
        @Get
        String findUser = "/user/findUser";
        @Post
        String createNewUser = "/user/createNewUser";
        @Get
        String getVerificationCode = "sms/getVerificationCode";
        @Get
        String submitVerificationCode = "sms/submitVerificationCode";
    }

    //===================参数key==========================================================

    /**
     * DESC: 参数的key
     * Created by jinphy, on 2017/12/4, at 23:55
     */
    public interface Key {
        String phone = "phone";
        String verificationCode = "verificationCode";
        String account = "account";
        String password = "password";
        String deviceId = "deviceId";
        String date = "date";
    }


    //===================请求结果==========================================================


    //===================网络回调==========================================================
}
