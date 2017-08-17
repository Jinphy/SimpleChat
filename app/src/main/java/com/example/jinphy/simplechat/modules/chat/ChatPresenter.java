package com.example.jinphy.simplechat.modules.chat;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.model.Message;
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


    private float downX;
    private float downY;
    Boolean moveVertical = null;


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            moveVertical = null;
//            view.handleVerticalTouchEvent(event);
            view.handleHorizontalTouchEvent(event);
            return false;
        } else {
            if (moveVertical == null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
//                        view.handleVerticalTouchEvent(event);
                        view.handleHorizontalTouchEvent(event);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getX() - downX);
                        float deltaY = Math.abs(event.getY() - downY);
                        if (deltaY+3 > deltaX) {
                            moveVertical = true;
//                            return view.handleVerticalTouchEvent(event);
                        } else {
                            moveVertical = false;
                            return view.handleHorizontalTouchEvent(event);
                        }
                    default:
                        return false;
                }

            } else if (moveVertical) {
//                return view.handleVerticalTouchEvent(event);
                return false;
            } else {
                return view.handleHorizontalTouchEvent(event);
            }

        }


    }

    @Override
    public void onBackPressed() {
        view.onBackPressed();
    }
}
