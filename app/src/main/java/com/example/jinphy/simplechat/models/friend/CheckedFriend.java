package com.example.jinphy.simplechat.models.friend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public class CheckedFriend {

    private Friend friend;

    private boolean checked;


    public static List<CheckedFriend> create(List<Friend> friends) {
        if (friends == null) {
            return new LinkedList<>();
        }
        List<CheckedFriend> checkedFriends = new ArrayList<>(friends.size());
        for (Friend friend : friends) {
            checkedFriends.add(new CheckedFriend(friend));
        }
        return checkedFriends;
    }

    private CheckedFriend(Friend friend) {
        this.friend = friend;
    }


    public Friend getFriend() {
        return friend;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getAccount() {
        return friend.getAccount();
    }

    public String getName() {
        return friend.getShowName();
    }


}
