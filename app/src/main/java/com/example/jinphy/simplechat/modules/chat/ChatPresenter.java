package com.example.jinphy.simplechat.modules.chat;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.message.Message;
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
                message.setSourceType(Message.SEND);
            } else {
                message.setSourceType(Message.RECEIVE);
            }
            int x  = random.nextInt(5);
            String contentType;
            switch (x) {
                case 0:
                    contentType = Message.TYPE_CHAT_FILE;
                    break;
                case 1:
                    contentType = Message.TYPE_CHAT_IMAGE;
                    break;
                case 2:
                    contentType = Message.TYPE_CHAT_TEXT;
                    break;
                case 3:
                    contentType = Message.TYPE_CHAT_VIDEO;
                    break;
                default:
                    contentType = Message.TYPE_CHAT_VOICE;
                    break;
            }
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

}
