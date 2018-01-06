package com.example.jinphy.simplechat.modules.main.msg;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;

import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface MsgContract {


    interface View extends BaseView<Presenter> {

        void  initFab(Activity activity);

        void fabAction(android.view.View view);

        RecyclerView getRecyclerView();

        void showChatWindow(MessageRecord item);
    }



    interface Presenter extends BasePresenter {

        <T> void handleItemEvent(android.view.View view, T item,int type,int position);

        List<MessageRecord> loadMsgRecord();

        MsgRecyclerViewAdapter getAdapter();
    }
}

