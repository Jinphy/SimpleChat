package com.example.jinphy.simplechat.modules.main;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.modules.main.friends.FriendsPresenter;
import com.example.jinphy.simplechat.modules.main.msg.MsgPresenter;
import com.example.jinphy.simplechat.modules.main.routine.RoutinePresenter;
import com.example.jinphy.simplechat.modules.main.self.SelfPresenter;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface MainContract {

    interface View extends BaseView<Presenter>{

        void selectFragment(android.view.View view);

        List<Fragment> generateFragments();

        MsgPresenter getMsgPresenter(Fragment view);

        FriendsPresenter getFriendsPresenter(Fragment view);

        RoutinePresenter getRoutinePresenter(Fragment view);

        SelfPresenter getSelfPresenter(Fragment view);

        void selectTab(int position,boolean setItem);

        void showNormalState(int position);

        void showSelectedState(int position);

        void animateBar(android.view.View view, float fromValue,float toValue,boolean showBar);

        void hideBar(android.view.View view);

        void showBar(android.view.View view);

        void setStatusBarColor(float factor);

        void initFab(int position);

        int currentItemPosition();

        void showMenu();

        void showUserInfo();

        void showFriendInfo(String account);

    }


    interface Presenter extends BasePresenter{

        void findUser(String account, BaseRepository.OnDataOk<Response<User>> callback);

        void checkAccount(Context context);

    }
}
