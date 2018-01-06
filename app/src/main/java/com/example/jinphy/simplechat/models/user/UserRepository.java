package com.example.jinphy.simplechat.models.user;

import android.content.Context;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.api.Api;
import com.example.jinphy.simplechat.api.ApiInterface;
import com.example.jinphy.simplechat.api.Response;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

/**
 * 一个用户仓库，管理着用户信息
 *
 * Created by jinphy on 2017/11/6.
 */
public class UserRepository  extends BaseRepository<User,User> implements UserDataSource<User> {

    private Box<User> userBox;

    public static UserRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    public static class InstanceHolder{
        static UserRepository DEFAULT = new UserRepository();
    }

    private UserRepository() {
        userBox = App.boxStore().boxFor(User.class);
    }


    @Override
    public void findUser(Context context, Task<User> task) {
        Api.<User>common(context)
                .path(Api.Path.findUser)
                .dataType(Api.Data.MODEL,User.class)
                .setup(api -> this.handleBuilder(api, task))
                .request();

    }

    @Override
    public void login(Context context, Task<User> task) {
        Api.<User>common(context)
                .hint("正在登录...")
                .path(Api.Path.login)
                .dataType(Api.Data.MODEL, User.class)
                .setup(api -> this.handleBuilder(api, task))
                .request();

    }

    @Override
    public void signUp(Context context, Task<User> task) {
        Api.<User>common(context)
                .hint("正在注册...")
                .path(Api.Path.signUp)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    private static final String TAG = "UserRepository";
    @Override
    public void saveUser(User user) {
        User first = userBox.query().equal(User_.account, user.getAccount()).build().findFirst();
        if (first != null) {
            user.setId(first.id);
        }
        userBox.put(user);
    }

    @Override
    protected void handleBuilder(ApiInterface<Response<User>> api, Task<User> task) {
        api.showProgress(task.isShowProgress())
                .useCache(task.isUseCache())
                .autoShowNo(task.isAutoShowNo())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk()==null?null: response -> task.getOnDataOk().call(response.getData()))
                .onResponseNo(task.getOnDataNo()==null?null: response -> task.getOnDataNo().call(TYPE_CODE))
                .onError(task.getOnDataNo()==null?null: e-> task.getOnDataNo().call(TYPE_ERROR))
                .onFinal(task.getOnFinal()==null?null: task.getOnFinal()::call);
    }

}
