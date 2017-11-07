package com.example.jinphy.simplechat.modules.chat;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.model.message.Message;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatPresenter implements ChatContract.Presenter {


    private ChatContract.View view;

    private List<Message> messages;

    public ChatPresenter(@NonNull ChatContract.View view) {
        this.view = Preconditions.checkNotNull(view);

    }



    @Override
    public void start() {

    }

    @Override
    public List<Message> loadMessages() {
        messages = new ArrayList<>(40);

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 40; i++) {
            Message message = new Message();
            if (i % 2 == 0) {
                message.setSourceType(Message.TYPE_SEND);
            } else {
                message.setSourceType(Message.TYPE_RECEIVE);
            }
            int contentType  = random.nextInt(5)+3;
            message.setContentType(contentType);
            messages.add(message);
        }
        return messages;
    }

    @Override
    public ChatRecyclerViewAdapter getAdapter() {
        return new ChatRecyclerViewAdapter(loadMessages());
    }

    @Override
    public int getItemCount() {
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        view.onBackPressed();
    }
}
