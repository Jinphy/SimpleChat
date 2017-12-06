package com.example.jinphy.simplechat.api;

import android.content.Context;

import org.java_websocket.WebSocket;

/**
 * API工厂类，专治各种不服
 *
 * Created by Jinphy on 2017/12/6.
 */

public abstract class Api {

    //===================工厂方法==========================================================
    /**
     * DESC: 创建一个通用网络请求API
     * Created by Jinphy, on 2017/12/6, at 13:03
     */
    public static ApiInterface common(Context context) {
        return CommonApi.create(context);
    }

    /**
     * DESC: 创建一个短信接口API
     * Created by Jinphy, on 2017/12/6, at 13:05
     */
    public static ApiInterface sms(Context context) {
        return SMSSDKApi.create(context);
    }


    //===================请求接口==========================================================
    /**
     * DESC: 网络请求路径
     * Created by jinphy, on 2017/12/4, at 21:38
     */
    public interface Path {
        String login = "/user/login";
        String findUser = "/user/findUser";
        String createNewUser = "/user/createNewUser";
        String getVerificationCode = "sms/getVerificationCode";
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
    /**
     * DESC: 网络请求结果类
     * Created by Jinphy, on 2017/12/6, at 13:06
     */
    public static class Response {
        public static String YES = "1";// 成功状态码
        public static String NO = "0";//  失败状态码
//        返回状态码
        private String code;
//        返回信息
        private String msg;
//        返回数据
        private String data;

        public Response() {

        }

        public Response(String code, String msg, String data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    //===================网络回调==========================================================

    /**
     * DESC: 请求成功回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnNext {
        void call(Response response);
    }

    /**
     * DESC: 请求异常回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnError {
        void call(Exception e);
    }

    /**
     * DESC: 连接打开时回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnOpen {
        void call(WebSocket webSocket);
    }

    /**
     * DESC: 连接打开时回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    public interface OnStart {
        void call();
    }

    /**
     * DESC: 请求结束时回调
     * Created by jinphy, on 2017/12/4, at 21:57
     */
    public interface OnClose {
        void call();
    }

}
