package com.example.jinphy.simplechat.modules.chat.models;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.chat.ChatAdapter;
import com.example.jinphy.simplechat.modules.chat.ChatPresenter;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DESC:
 * Created by jinphy on 2018/4/1.
 */

public class MyData {

    public  Group withGroup;

    public Friend withFriend;

    public Member memberOfMe;

    public String with;

    public String owner;

    public ChatAdapter adapter;

    public MyAdapter<BottomExtraView.MenuItem> bottomMenuAdapter;

    public final boolean isChatWithFriend;

    public boolean exit;

    /**
     * DESC: 缓存正在发送的或者正在下载语音信息的消息
     * Created by jinphy, on 2018/3/26, at 9:29
     */
    public final Map<Long, Message> msgMap = new ConcurrentHashMap<>();

    public static MyData init(String with) {
        return new MyData(with);
    }

    private MyData(String with) {
        if (ObjectHelper.isTrimEmpty(with)) {
            ObjectHelper.throwIlleagalArgs("with account cannot be null");
        }
        this.with = with;
        isChatWithFriend = !with.contains("G");
    }

    public void update(ChatPresenter presenter) {
        this.owner = presenter.getOwner();
        if (isChatWithFriend) {
            withFriend = presenter.getFriend(with);
        } else {
            withGroup = presenter.getGroup(with);
            memberOfMe = presenter.getSelfMember(with);
        }
        adapter = new ChatAdapter(presenter.getUserAvatar(), with);

        bottomMenuAdapter = MyAdapter.<BottomExtraView.MenuItem>newInstance()
                .onInflate(viewType -> R.layout.chat_bottom_menu_item)
                .data(BottomExtraView.MenuItem.create())
                .onCreateView(holder -> {
                    holder.imageViews(R.id.icon_view);
                    holder.textViews(R.id.tag_view);
                })
                .onBindView((holder, item, position) -> {
                    holder.imageView[0].setImageResource(item.icon);
                    holder.textView[0].setText(item.tag);
                    holder.setClickedViews(holder.imageView[0]);
                })
                .make();
    }
}
