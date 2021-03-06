package com.example.jinphy.simplechat.models.message;

import com.example.jinphy.simplechat.base.BaseRepository;

import java.util.Collection;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public interface MessageDataSource {


    /**
     * DESC: 保存接收的信息
     * Created by jinphy, on 2018/1/18, at 9:01
     */
    void saveReceive(Message... messages);


    /**
     * DESC: 保存接收的信息
     * Created by jinphy, on 2018/1/18, at 9:01
     */
    void saveReceive(List<Message> messages);

    /**
     * DESC: 保存发送的信息
     * Created by jinphy, on 2018/3/4, at 15:19
     */
    void saveSend(Message message);

    /**
     * DESC: 更新消息
     * Created by jinphy, on 2018/1/18, at 9:02
     */
    void update(Message... messages);

    void update(List<Message> messages);

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:05
     */
    void delete(Message... messages);

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:06
     */
    void delete(long... ids);

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:08
     */
    void delete(Collection<Message> messages);

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:27
     */
    void delete(String owner);

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:31
     */
    void delete(String owner, String with);

    void delete(String owner, String with, String contentType);

    /**
     * DESC: 获取最新的消息
     * Created by jinphy, on 2018/4/3, at 9:13
     */
    Message getLast(String owner, String with);

    Message get(long id);

    /**
     * DESC: 加载消息
     *
     * @param owner 消息的拥有者，是一个账号，即该消息属于哪个账户的
     * @param with  消息的聊天对象，是一个账号
     *              <p>
     *              Created by jinphy, on 2018/1/18, at 9:11
     */
    List<Message> load(String owner, String with);

    /**
     * DESC: 加载新消息
     * Created by jinphy, on 2018/3/19, at 10:06
     */
    List<Message> loadNew(String owner, String with);

    List<Message> loadSystemMsg(String owner, String contentType);

    void sendMsg(BaseRepository.Task<String> task);
}
