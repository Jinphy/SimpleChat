package com.example.jinphy.simplechat.api;

import android.text.TextUtils;

import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * DESC：网络请求参数类，包可见
 *
 *      使用说明：该类时网络请求参数封装类，用来操作请求参数
 *      1、参数设置是安全的，你可以设置任何类型的参数，例如int、float、等，同时参数设置是安全的
 *          你可以设置空值，此时该设置将被忽略而不会影响网络请求的正确性，这对非必传的参数是非常有用的

 *      2、公共参数将会在创建该对象的时候被自动添加
 *
 *      3、该类会在内部自动执行编码、签名、和AES加密，你要做的就只是设置你要传的参数（除了公共参数）
 *
 *
 * Created by Jinphy on 2017/12/25.
 */

class Params extends HashMap<String, String> {



    //----------------------------------------------------------------------------------------------

    /**
     * DESC: 获取一个请求参数的实例
     * Created by Jinphy, on 2017/12/25, at 17:06
     */
    public static Params newInstance() {
        return new Params();
    }

    /**
     * DESC: 创建请求参数，并添加公共参数
     * Created by Jinphy, on 2017/12/25, at 18:37
     */
    private Params(){

    }


    /**
     * DESC: 添加参数
     *
     *
     * Created by Jinphy, on 2017/12/25, at 20:25
     */
    @Override
    public String put(String key, String value) {
        if (check(key, value)) {
            return super.put(key, value);
        }
        return null;
    }

    /**
     * DESC: 添加参数
     *
     *
     * Created by Jinphy, on 2017/12/25, at 20:25
     */
    public String put(String key, Object value) {
        if (check(key, value)) {
            return super.put(key, value.toString());
        }
        return null;
    }

    public String getEncrypted(Object key) {
        String value = this.get(key);
        try {
            // 编码
            value = URLEncoder.encode(value,"UTF-8");
            // 加密
            value = EncryptUtils.aesEncrypt(value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * DESC: 将参数的key和value拼接后统一加密成一个加密串
     * Created by jinphy, on 2018/1/1, at 12:44
     */
    @Override
    public String toString() {
        int size = size();
        if (size == 0) {
            return "";
        }
        String[] params = new String[size];
        int i=0;
        for (Entry<String, String> param : entrySet()) {
            params[i++] = param.getKey() + "=" + param.getValue();
        }
        String content = TextUtils.join("&", params);
        try {
            // 编码
            content = URLEncoder.encode(content, "UTF-8");
            // 加密
            content = EncryptUtils.aesEncrypt(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * DESC: 判断请求参数是否合法
     * Created by Jinphy, on 2017/12/25, at 17:49
     */
    public final boolean check(String key, Object value) {
        if (ObjectHelper.isTrimEmpty(key) ||
                ObjectHelper.isNull(value) ||
                ObjectHelper.isTrimEmpty(value.toString())) {
            return false;
        }
        return true;
    }
}
