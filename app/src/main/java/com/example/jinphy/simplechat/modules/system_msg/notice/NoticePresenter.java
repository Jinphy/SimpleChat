package com.example.jinphy.simplechat.modules.system_msg.notice;

import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class NoticePresenter implements NoticeContract.Presenter{

    private final NoticeContract.View view;
    private MessageRepository messageRepository;
    private UserRepository userRepository;


    public NoticePresenter(NoticeContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public NoticeRecyclerViewAdapter getAdapter() {
        User user = userRepository.currentUser();
        List<Message> messages = messageRepository.loadSystemMsg(
                user.getAccount(), Message.TYPE_SYSTEM_NOTICE);
        return new NoticeRecyclerViewAdapter(messages);
    }
}
