package com.example.jinphy.simplechat.api;

/**
 * Created by jinphy on 2017/11/7.
 */

public class Response {

    public static final String yes = "yes";
    public static final String no = "no";
    public static final String error = "error";
    public Object message;

    public Object object;

    public Response(Object message) {
        this.message = message;
    }

    public Response(Object message, Object object) {
        this.message = message;
        this.object = object;
    }
}
