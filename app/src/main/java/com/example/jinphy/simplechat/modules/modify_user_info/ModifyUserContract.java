package com.example.jinphy.simplechat.modules.modify_user_info;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.user.User;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/1/7.
 */

public interface ModifyUserContract {


    interface View extends BaseView<Presenter> {


        /**
         * DESC: 以验证码的方式修改密码
         * Created by jinphy, on 2018/1/9, at 16:59
         */
        void modifyPasswordWithCode();


        /**
         * DESC: 以旧密码的方式修改密码
         * Created by jinphy, on 2018/1/9, at 18:14
         */
        void modifyPasswordWithOld();

        void selectSex();

        void pickPhoto();

        void whenModifyUserInfoSucceed();

        String getModifiedPasswordAES();
    }

    interface Presenter extends BasePresenter {

        User getUser();
        /**
         * DESC: 获取验证码
         * Created by jinphy, on 2018/1/9, at 16:53
         */
        void getCode(Context context, String phone,BaseRepository.OnDataOk<Response<String>> onDataOk);

        /**
         * DESC: 提交验证码
         * Created by jinphy, on 2018/1/9, at 16:54
         */
        void submitCode(Context context, String phone, String code, BaseRepository.OnDataOk<Response<String>> onDataOk);

        void modifyUserInfo(Context context, Map<String, Object> params);
    }


}
