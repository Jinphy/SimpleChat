package com.example.jinphy.simplechat.models.api.common;

/**
 * DESC:
 * Created by jinphy on 2017/12/31.
 */

class Body {
    public String requestId;
    public String content;


    public static Body create(String requestId, Params params) {
        Body body = new Body();
        body.requestId = requestId;
        body.content = params.toString();
        return body;
    }


}
