package com.example.jinphy.simplechat.models.api.common;

import android.content.Context;

import com.example.jinphy.simplechat.annotations.Get;
import com.example.jinphy.simplechat.annotations.Post;

/**
 * API工厂类，专治各种不服
 * <p>
 * Created by Jinphy on 2017/12/6.
 */

public abstract class Api {

    // 宿舍WiFi
//            public static String BASE_URL = "ws://192.168.0.3";
    //    成和WiFi
    //    public static String BASE_URL = "ws://192.168.3.21";
    //    我的手机WiFi
    public static String BASE_URL = "ws://192.168.43.224";
    //    公司WiFi
    //    public static String BASE_URL = "ws://172.16.11.134";
    // 简阅书吧 wifi
//        public static String BASE_URL = "ws://192.168.1.199";

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

    /**
     * DESC: 文件传输服务端口
     * Created by jinphy, on 2018/1/19, at 12:07
     */
    public static String FILE_PORT = "4543";

    //===================工厂方法==========================================================

    /**
     * DESC: 创建一个通用网络请求API
     * Created by Jinphy, on 2017/12/6, at 13:03
     */
    public static <U> ApiInterface<Response<U>> common(Context context) {
        return CommonApi.create(context);
    }

    /**
     * DESC: 创建一个合并网络请求Api
     * Created by Jinphy, on 2017/12/6, at 13:03
     */
    public static <U> ApiInterface<Response<U>[]> zipper(Context context) {
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
        @Post
        String logout = "/user/logout";
        @Get
        String findUser = "/user/findUser";
        @Post
        String signUp = "/user/signUp";
        @Get
        String getVerificationCode = "sms/getVerificationCode";
        @Get
        String submitVerificationCode = "sms/submitVerificationCode";
        @Post
        String modifyUserInfo = "/user/modifyUserInfo";
        @Post
        String addFriend = "/friend/addFriend";
        @Post
        String loadFriends = "/friend/loadFriends";
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
        String avatar = "avatar";
        String name = "name";
        String accessToken = "accessToken";
        String signature = "signature";
        String sex = "sex";
        String address = "address";
        String requestAccount = "requestAccount";
        String receiveAccount = "receiveAccount";
        String remark = "remark";
        String verifyMsg = "verifyMsg";
        String owner = "owner";
    }


    /**
     * DESC: 网络请求返回数据类型的枚举类
     *
     *      该枚举对应于{@link Response#data} 的数据类型
     *
     *      在执行网络请求时可以通过调用{@link ApiInterface#dataType(Data, Class[])} 来设置{@code data}
     *      的返回类型
     *
     *      例如：
     *        Api.<List<GSUser>>common(context)
     *                    .dataType(Api.Data.MODEL_LIST,GSUser.class)
     *                    .path(Api.Path.login)
     *                    .onResponseYes(response -> {
     *                          List<GSUser> data = response.getBitmapFromActivity();
     *                    })
     *                    .request();
     *      注意：
     *          1、需要在common方法前加上尖括号，并指定需要的返回的data的类型，例如：List<GSUser>
     *          2、调用方法{@link ApiInterface#dataType(Data, Class[])},根据指定类型选择对应的枚举常量
     *          3、在上面示例代码中的{@code onResponseYes()}中，就可以直接调用Response类的对象
     *              response的方法getData()获取指定类型的data数据，{@link Response#getData()}
     *
     *
     * Created by jinphy, on 2018/1/5, at 13:47
     */
    public enum Data {


        /**
         * DESC: 当data 类型为具体的某个model类型，例如：GSUser类时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MODEL, GSUser.class);
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:06
         */
        MODEL,

        /**
         * DESC: 当data类型为Map<String,String>时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MAP)
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:11
         */
        MAP,

        /**
         * DESC: 当data类型为某个model数组类型，例如：GSUser[]时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MODEL_ARRAY, GSUser[].class)
         *
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:13
         */
        MODEL_ARRAY,

        /**
         * DESC: 当data类型为Map<String,String>[] 时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MAP_ARRAY)
         *
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:14
         */
        MAP_ARRAY,

        /**
         * DESC: 当data类型为某个model的list列表，例如List<GSUser>时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MODEL_LIST, GSUser.class)
         *
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:15
         */
        MODEL_LIST,

        /**
         * DESC: 当data类型为List<Map<String,String>>时，使用该枚举
         *
         * 函数调用：dataType(Api.Data.MAP_LIST)
         *
         * @see ApiInterface#dataType(Data, Class[])
         * @see BaseApi#dataType(Data, Class[])
         * Created by jinphy, on 2018/1/5, at 14:16
         */
        MAP_LIST,

    }
}
