package com.example.jinphy.simplechat.modules.main.msg;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;

import java.util.List;
import java.util.PrimitiveIterator;

/**
 * Created by jinphy on 2017/8/10.
 */

public interface MsgContract {


    interface View extends BaseView<Presenter> {

        RecyclerView getRecyclerView();

        void showChatWindow(MessageRecord item);
    }



    interface Presenter extends BasePresenter {

        List<MessageRecord> loadMsgRecords();

        void deleteMsgRecord(MessageRecord record);

        void clearMsg(MessageRecord record);

        void updateRecord(MessageRecord record);

        String getName(String account);
    }
}

