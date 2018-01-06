package com.example.jinphy.simplechat.models.verification_code;

import android.content.Context;

import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.api.ApiInterface;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.base.BaseRepository;

/**
 * DESC: 验证码仓库类
 * Created by jinphy on 2018/1/6.
 */

public class CodeRepository extends BaseRepository<String,String> implements CodeDataSource<String>{


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
                .hint("正在获取...")
                .path(Api.Path.getVerificationCode)
                .setup(api -> this.handleBuilder(api, task))
                .request();

    }

    /**
     * DESC: 提交验证码
     * Created by jinphy, on 2018/1/6, at 13:13
     */
    @Override
    public void verify(Context context, Task<String> task) {
        Api.sms(context)
                .hint("验证中...")
                .path(Api.Path.submitVerificationCode)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    @Override
    protected void handleBuilder(ApiInterface<Response<String>> api, Task<String> task) {
        api.showProgress(task.isShowProgress())
                .autoShowNo(task.isAutoShowNo())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk()==null
                        ?null
                        :response -> task.getOnDataOk().call(response.getMsg())
                )
                .onResponseNo(task.getOnDataNo()==null
                        ?null
                        :response -> task.getOnDataNo().call(BaseRepository.TYPE_CODE)
                )
                .onError(task.getOnDataNo()==null?null: e-> task.getOnDataNo().call(BaseRepository.TYPE_ERROR))
                .onFinal(task.getOnFinal()==null?null: task.getOnFinal()::call);
    }
}
