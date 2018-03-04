package com.example.jinphy.simplechat.models.api.send;

/**
 * DESC: 发送消息的结果类
 * Created by jinphy on 2018/1/19.
 */
public class SendResult<T> {

    public static final String OK = "200";
    public static final String NO = "100";


    /**
     * DESC: 发送返回码
     * Created by jinphy, on 2018/1/19, at 9:45
     */
    public String code;

    /**
     * DESC: 发送任务
     * Created by jinphy, on 2018/1/19, at 9:45
     */
    public T data;

    public SendResult(String code, T data) {
        this.code = code;
        this.data = data;
    }
}
