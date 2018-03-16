package com.example.jinphy.simplechat.models.verification_code;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BaseRepository;

/**
 * DESC: 验证码仓库类
 * Created by jinphy on 2018/1/6.
 */

public class CodeRepository extends BaseRepository implements CodeDataSource{


    public static CodeRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    public static class InstanceHolder{
        static CodeRepository DEFAULT = new CodeRepository();
    }



    /**
     * DESC: 获取验证码
     * Created by jinphy, on 2018/1/6, at 13:13
     */
    @Override
    public void getCode(Context context,Task<String> task) {
        Api.sms(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在获取...")
                .path(Api.Path.getVerificationCode)
                .request();

    }

    /**
     * DESC: 提交验证码
     * Created by jinphy, on 2018/1/6, at 13:13
     */
    @Override
    public void verify(Context context, Task<String> task) {
        Api.sms(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("验证中...")
                .path(Api.Path.submitVerificationCode)
                .request();
    }

}
