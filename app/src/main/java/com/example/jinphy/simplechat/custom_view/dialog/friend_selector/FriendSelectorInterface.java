package com.example.jinphy.simplechat.custom_view.dialog.friend_selector;

import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public interface FriendSelectorInterface<T> {

    FriendSelectorInterface<T> width(int dbValue);

    FriendSelectorInterface<T> height(int dbValue);

    FriendSelectorInterface<T> cancelable(boolean flag);

    FriendSelectorInterface<T> title(CharSequence title);

    FriendSelectorInterface<T> cancelColor(int color);

    FriendSelectorInterface<T> titleColor(int color);

    FriendSelectorInterface<T> confirmColor(int color);

    FriendSelectorInterface<T> exclude(List<String> exclude);

    FriendSelectorInterface<T> exclude(String... exclude);

    FriendSelectorInterface<T> onSelect(SChain.Consumer<List<CheckedFriend>> onSelect);

    FriendSelector display();

}
