package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.main.MainFragment;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface MsgContract {


    interface View extends BaseView<Presenter> {

        void setMainFragment(@NonNull MainFragment mainFragment);

        void  initFab();

        void fabAction(android.view.View view);

        RecyclerView getRecyclerView();

        void showChatWindow(MsgRecord item);
    }



    interface Presenter extends BasePresenter {

        void handleItemEvent(android.view.View view, MsgRecord item);

        List<MsgRecord> loadMsgRecord();

        MsgRecyclerViewAdapter getAdapter();
    }
}

