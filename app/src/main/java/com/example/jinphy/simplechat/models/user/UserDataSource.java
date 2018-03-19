package com.example.jinphy.simplechat.models.user;

import android.content.Context;

import com.example.jinphy.simplechat.base.BaseRepository;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by jinphy on 2017/11/6.
 */
public interface UserDataSource {

    void findUser(Context context, BaseRepository.Task<User> task);

    void login(Context context, BaseRepository.Task<User> task);

    void signUp(Context context, BaseRepository.Task<User> task);

    void modifyUserInfo(Context context, BaseRepository.Task<Map<String,String>> task);

    void logout(Context context, BaseRepository.Task<Map<String, String>> task);

    void logoutLocal();

    void saveUser(User user);

    void updateUser(User user);

    /**
     * DESC: 更新当前账号信息
     * Created by jinphy, on 2018/1/7, at 11:07
     */
    void updateUser(Map<String,String> values);

    /**
     * DESC: 删除账号
     * Created by jinphy, on 2018/1/7, at 11:28
     */
    void deleteUser(User user);

    /**
     * DESC: 返回设备当前使用的和用户
     * Created by jinphy, on 2018/1/7, at 11:10
     */
    User currentUser();

    /**
     * DESC: 查询所有账户
     * Created by jinphy, on 2018/1/7, at 11:29
     */
    List<User> allUsers();

    /**
     * DESC: 判断当前当前账号是否处于登录状态
     * Created by jinphy, on 2018/1/7, at 11:11
     */
    boolean hasLogin();

    boolean rememberPassword();

    boolean needToLogin();

    void getAvatar(Context context, BaseRepository.Task<Map<String, String>> task);


    void loadAvatars(Context context, BaseRepository.Task<List<Map<String, String>>> task);


    void checkAccount(Context context);


}
