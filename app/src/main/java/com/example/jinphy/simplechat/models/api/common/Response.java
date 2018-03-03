package com.example.jinphy.simplechat.models.api.common;

/**
 * DESC: 网络请求结果类
 * Created by Jinphy, on 2017/12/6, at 13:06
 */
public class Response<T> {

    //----------返回码 --------------------------------------------------------------------------------------
    public static final String YES = "200";
    public static final String NO = "10000";

    // 以 3 开头的错误为接口请求信息错误
    public static final String NO_FIND_USER = "30001";
    public static final String NO_CREATE_USER = "30002";
    public static final String NO_LOGIN = "30003";
    public static final String NO_GET_CODE = "30004";
    public static final String NO_SUBMIT_CODE = "30005";
    public static final String NO_ACCESS_TOKEN = "30006";

    // 以4 开头的为客户端错误
    public static final String NO_API_NOT_FUND = "40001";
    public static final String NO_PARAMS_MISSING = "40002";

    // 以5 开头的为服务器错误
    public static final String NO_SERVER = "50001";

    //-------------------------------------------------------------------------------------------------
    //        返回状态码
    private String code;
    //        返回信息
    private String msg;
    //        返回数据
    private T data;

    public Response(Class<T> tClass) {

    }

    public Response(String code, String msg, T data) {
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static <U> Class<Response<U>> responseClass(Class<U> uClass) {
        return (Class<Response<U>>) new Response<>(uClass).getClass();
    }
}
