package com.example.jinphy.simplechat.models.message_record;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public interface MessageRecordDataSource {


    void saveOrUpdate(MessageRecord record);

    MessageRecord get(String owner, Friend friend);

    List<MessageRecord> load(String owner);

    void delete(long... ids);

    void delete(MessageRecord... records);

    void delete(String owner, Friend width);

}
