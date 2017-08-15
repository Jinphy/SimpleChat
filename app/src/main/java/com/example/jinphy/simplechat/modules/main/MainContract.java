package com.example.jinphy.simplechat.modules.main;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.modules.main.friends.FriendsContract;
import com.example.jinphy.simplechat.modules.main.friends.FriendsPresenter;
import com.example.jinphy.simplechat.modules.main.msg.MsgContract;
import com.example.jinphy.simplechat.modules.main.msg.MsgFragment;
import com.example.jinphy.simplechat.modules.main.msg.MsgPresenter;
import com.example.jinphy.simplechat.modules.main.routine.RoutineContract;
import com.example.jinphy.simplechat.modules.main.routine.RoutinePresenter;
import com.example.jinphy.simplechat.modules.main.self.SelfContract;
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

        void setToolbarAlpha(float faction);

        void setHeadViewTransY(float faction,int baseTransY,int distance);

        void initFab(int position);

        int currentItemPosition();

        boolean dispatchTouchEvent(MotionEvent event);

    }


    interface Presenter extends BasePresenter{

        boolean dispatchTouchEvent(MotionEvent event);
    }
}
