package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgPresenter implements MsgContract.Presenter {
    MsgContract.View view;
    MessageRecordRepository recordRepository;
    UserRepository userRepository;
    private List<MessageRecord> messageRecords;

    public MsgPresenter(@NonNull MsgContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.recordRepository = MessageRecordRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }


    @Override
    public <T>void handleItemEvent(View view, T item,int type,int position) {
        switch (view.getId()) {
            case R.id.item_view:
                MsgPresenter.this.view.showChatWindow((MessageRecord) item);
                break;
        }
    }

    @Override
    public List<MessageRecord> loadMsgRecord() {
        messageRecords = recordRepository.load(userRepository.currentUser().getAccount());
        return messageRecords;
    }

    @Override
    public MsgRecyclerViewAdapter getAdapter() {
        MsgRecyclerViewAdapter adapter = new MsgRecyclerViewAdapter(loadMsgRecord());
        adapter.onClick(this::handleItemEvent);
        return adapter;
    }
}
