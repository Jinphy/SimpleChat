package com.example.jinphy.simplechat.models.user;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;

import java.util.List;
import java.util.Map;

import io.objectbox.Box;

/**
 * 一个用户仓库，管理着用户信息
 *
 * Created by jinphy on 2017/11/6.
 */
public class UserRepository  extends BaseRepository implements UserDataSource {

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
    public void logout(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .hint("正在注销...")
                .path(Api.Path.logout)
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

    @Override
    public void modifyUserInfo(Context context, Task<Map<String,String>> task) {
        Api.<Map<String,String>>common(context)
                .hint("正在修改...")
                .path(Api.Path.modifyUserInfo)
                .dataType(Api.Data.MAP)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    /**
     * DESC: 登录成功时保存账号信息，并将设备上的其他账号的当前账号状态设置false
     *
     *      注意：
     *      1、只有在登录成功时才会改变当前使用的账号，其他比如退出登录操作时当前账号不变
     *      切换账号时必须先退出当前账号，然后登录新账号。
     *
     *      2、退出账号时执行退出登录接口并根据返回来的user对象
     *      更新数据库：调用{@link UserRepository#updateUser(Map)}
     * Created by jinphy, on 2018/1/7, at 10:53
     */
    @Override
    public void saveUser(User user) {
        User current = userBox.query().equal(User_.current, true).build().findFirst();
        if (current != null) {
            current.setCurrent(false);
            userBox.put(current);
        }

        User old = userBox.query().equal(User_.account, user.getAccount()).build().findFirst();
        if (old != null) {
            user.setId(old.id);
        }
        user.setCurrent(true);
        userBox.put(user);
    }

    /**
     * DESC: 更新当前账号信息
     * Created by jinphy, on 2018/1/7, at 11:07
     */
    @Override
    public void updateUser(Map<String, String> values) {
        User current = userBox.query().equal(User_.account, values.remove(User_.account.name)).build().findFirst();
        if (current == null) {
            return;
        }
        String column;
        for (Map.Entry<String, String> update : values.entrySet()) {

            column = update.getKey();
            if (User_.accessToken.name.equals(column)) {
                current.setAccessToken(update.getValue());
            } else if (User_.avatar.name.equals(column)) {
                current.setAvatar(update.getValue());
            } else if (User_.name.name.equals(column)) {
                current.setName(update.getValue());
            } else if (User_.signature.name.equals(column)) {
                current.setSignature(update.getValue());
            } else if (User_.sex.name.equals(column)) {
                current.setSex(update.getValue());
            } else if (User_.password.name.equals(column)) {
                current.setPassword(update.getValue());
            } else if (User_.address.name.equals(column)) {
                current.setAddress(update.getValue());
            }
        }
        userBox.put(current);
    }

    /**
     * DESC: 删除账号
     * Created by jinphy, on 2018/1/7, at 11:28
     */
    @Override
    public void deleteUser(User user) {
        userBox.remove(user);
    }

    /**
     * DESC: 返回设备当前使用的和用户
     * Created by jinphy, on 2018/1/7, at 11:10
     */
    @Override
    public User currentUser() {
        return userBox.query().equal(User_.current, true).build().findFirst();
    }

    /**
     * DESC: 查询所有账户
     * Created by jinphy, on 2018/1/7, at 11:29
     */
    @Override
    public List<User> allUsers() {
        return userBox.getAll();
    }

    /**
     * DESC: 判断当前当前账号是否处于登录状态
     * Created by jinphy, on 2018/1/7, at 11:11
     */
    @Override
    public boolean hasLogin() {
        return userBox.query()
                .equal(User_.current, true)
                .equal(User_.status, User.STATUS_LOGIN)
                .build()
                .findFirst() != null;
    }

    /**
     * DESC: 判断是否记住密码，不能直接用是否有密码来判断，因为用户在登录时可能是用验证码登录的，这时没有密码
     * Created by jinphy, on 2018/1/7, at 12:03
     */
    @Override
    public boolean rememberPassword() {
        User user = currentUser();
        return user != null && user.isRememberPassword();
    }

    /**
     * DESC: 判断用户是否需要执行登录操作，在每次进入欢迎页时判断
     *
     *  两种情况需要登录：
     *      1、没有记住密码
     *      2、记住密码了但是退出登录了
     *
     * Created by jinphy, on 2018/1/7, at 12:04
     */
    @Override
    public boolean needToLogin() {
        return !rememberPassword() || !hasLogin();
    }

    @Override
    public void logoutLocal() {
        User user = currentUser();
        user.setStatus(User.STATUS_LOGOUT);
        userBox.put(user);
    }

    @Override
    protected<T> void handleBuilder(ApiInterface<Response<T>> api, Task<T> task) {
        api.showProgress(task.isShowProgress())
                .useCache(task.isUseCache())
                .autoShowNo(task.isAutoShowNo())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk()==null?null: response -> task.getOnDataOk().call(response))
                .onResponseNo(task.getOnDataNo()==null?null: response -> task.getOnDataNo().call(TYPE_CODE))
                .onError(task.getOnDataNo()==null?null: e-> task.getOnDataNo().call(TYPE_ERROR))
                .onFinal(task.getOnFinal()==null?null: task.getOnFinal()::call);
    }

}
