package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgPresenter implements MsgContract.Presenter {
    MsgContract.View view;

    private List<MsgRecord> msgRecords;

    public MsgPresenter(@NonNull MsgContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }

    @Override
    public void start() {

    }


    @Override
    public <T>void handleItemEvent(View view, T item,int type,int position) {
        switch (view.getId()) {
            case R.id.item_view:
                MsgPresenter.this.view.showChatWindow((MsgRecord) item);
                break;
        }
    }

    @Override
    public List<MsgRecord> loadMsgRecord() {
        msgRecords = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            msgRecords.add(new MsgRecord());
        }
        return msgRecords;
    }

    @Override
    public MsgRecyclerViewAdapter getAdapter() {
        MsgRecyclerViewAdapter adapter = new MsgRecyclerViewAdapter(loadMsgRecord());
        adapter.onClick(this::handleItemEvent);
        return adapter;
    }
}
