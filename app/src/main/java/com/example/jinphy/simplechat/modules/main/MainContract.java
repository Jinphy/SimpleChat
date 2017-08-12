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

        MsgPresenter getMsgPresenter(MsgContract.View view);

        FriendsPresenter getFriendsPresenter(FriendsContract.View view);

        RoutinePresenter getRoutinePresenter(RoutineContract.View view);

        SelfPresenter getSelfPresenter(SelfContract.View view);

        void selectTab(int position,boolean setItem);

        void showNormalState();

        void showSelectedState();

        void animateBar(android.view.View view, float fromValue,float toValue,boolean showBar);

        void hideBar(android.view.View view);

        void showBar(android.view.View view);

        void setToolbarAlpha(float faction);

        void setHeadViewTransY(float faction,int baseTransY,int distance);

        void initFab();

        int currentItemPosition();

        boolean dispatchTouchEvent(MotionEvent event);

    }


    interface Presenter extends BasePresenter{

        boolean dispatchTouchEvent(MotionEvent event);
    }
}
