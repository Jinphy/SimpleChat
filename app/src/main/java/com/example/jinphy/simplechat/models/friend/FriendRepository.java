package com.example.jinphy.simplechat.models.friend;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiCallback;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.event_bus.EBBase;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;
import java.util.Map;

import io.objectbox.Box;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class FriendRepository extends BaseRepository implements FriendDataSource {

    private Box<Friend> friendBox;


    private static class InstanceHolder{
        static final FriendRepository DEFAULT = new FriendRepository();
    }

    public static FriendRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }


    private FriendRepository() {
        friendBox = App.boxStore().boxFor(Friend.class);
    }

    /**
     * DESC: 本地加载
     * Created by jinphy, on 2018/2/28, at 18:28
     */
    @Override
    public List<Friend> loadLocal(String owner, String... account) {
        if (account.length == 0) {
            return friendBox.query()
                    .filter(friend->{
                        if (StringUtils.equal(owner, friend.getOwner())) {
                            switch (friend.getStatus()) {
                                case Friend.status_ok:
                                case Friend.status_black_listed:
                                case Friend.status_black_listing:
                                    return true;
                            }
                        }
                        return false;
                    })
                    .build().find();
        } else {
            return friendBox.query()
                    .filter(friend -> {
                        if (StringUtils.equal(owner, friend.owner)) {
                            for (String a : account) {
                                if (StringUtils.equal(a, friend.account)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .build().find();
        }
    }

    @Override
    public List<Friend> loadExclude(String owner, List<String> excludeAccount) {
        return friendBox.query()
                .filter(friend->{
                    if (StringUtils.equal(owner, friend.getOwner())) {
                        switch (friend.getStatus()) {
                            case Friend.status_ok:
                            case Friend.status_black_listed:
                            case Friend.status_black_listing:
                                for (String s : excludeAccount) {
                                    if (StringUtils.equal(s, friend.getAccount())) {
                                        return false;
                                    }
                                }
                                return true;
                        }
                    }
                    return false;
                })
                .build().find();

    }

    /**
     * DESC: 从服务器中获取好友列表
     * Created by jinphy, on 2018/2/28, at 18:29
     */
    @Override
    public void loadOnline(Context context, Task<List<Map<String,String>>> task) {
        Api.<List<Map<String,String>>>common(context)
                .path(Api.Path.loadFriends)
                .cancellable(false)
                .showProgress(false)
                .dataType(Api.Data.MAP_LIST)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    /**
     * DESC: 获取指定的好友
     *
     * @param whenOk 当获取成功后回调
     * Created by jinphy, on 2018/3/4, at 12:52
     */
    @Override
    public void getOnline(Context context, String owner, String account, SChain.Consumer<Friend>... whenOk) {
        EBBase<Friend> flag = new EBBase<>(false, null);
        Api.<Map<String, String>>common(context)
                .path(Api.Path.getFriend)
                .param(Api.Key.owner, owner)
                .param(Api.Key.account, account)
                .cancellable(false)
                .showProgress(false)
                .autoShowNo(false)
                .dataType(Api.Data.MAP)
                .onResponseYes(response -> {
                    Friend friend = Friend.parse(response.getData());
                    flag.data = friend;
                    save(friend);
                    synchronized (flag) {
                        if (flag.ok) {
                            for (SChain.Consumer<Friend> consumer : whenOk) {
                                if (consumer != null) {
                                    consumer.accept(friend);
                                }
                            }
                        } else {
                            flag.ok = true;
                        }
                    }
                })
                .request();
        Api.<Map<String,String>>common(context)
                .path(Api.Path.getAvatar)
                .param(Api.Key.account, account)
                .cancellable(false)
                .showProgress(false)
                .autoShowNo(false)
                .dataType(Api.Data.MAP)
                .onResponseYes(response -> {
                    Map<String, String> data = response.getData();
                    if (!"无".equals(data.get(User_.avatar.name))) {
                        Bitmap bitmap = StringUtils.base64ToBitmap(data.get(User_.avatar.name));
                        ImageUtil.storeAvatar(account, bitmap);
                    }
                })
                .onFinal(() -> {
                    synchronized (flag) {
                        if (flag.ok) {
                            for (SChain.Consumer<Friend> consumer : whenOk) {
                                if (consumer != null) {
                                    consumer.accept(flag.data);
                                }
                            }
                        } else {
                            flag.ok = true;
                        }
                    }
                })
                .request();
    }

    /**
     * DESC: 查询指定账号的好友数
     * Created by jinphy, on 2018/2/28, at 19:16
     */
    @Override
    public long count(String owner) {
        return friendBox.query()
                .equal(Friend_.owner, owner)
                .build()
                .count();
    }

    @Override
    public Friend get(String owner, String account) {
        if (TextUtils.isEmpty(owner) || TextUtils.isEmpty(account)) {
            return null;
        }
        return friendBox.query()
                .equal(Friend_.owner, owner)
                .equal(Friend_.account, account)
                .build().findFirst();
    }

    @Override
    public void addFriend(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在申请...")
                .path(Api.Path.addFriend)
                .dataType(Api.Data.MAP)
                .request();
    }

    @Override
    public void modifyRemark(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在修改...")
                .path(Api.Path.modifyRemark)
                .dataType(Api.Data.MAP)
                .request();
    }

    @Override
    public void modifyStatus(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在修改...")
                .path(Api.Path.modifyStatus)
                .dataType(Api.Data.MAP)
                .request();
    }

    @Override
    public void deleteFriend(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在删除...")
                .path(Api.Path.deleteFriend)
                .dataType(Api.Data.MAP)
                .request();

    }

    @Override
    public void save(List<Friend> friends) {
        if (friends == null || friends.size() == 0) {
            return;
        }
        for (Friend friend : friends) {
            save(friend);
        }
    }

    public void save(Friend friend) {
        if (friend == null) {
            return;
        }
        Friend old = friendBox.query()
                .equal(Friend_.account, friend.getAccount())
                .equal(Friend_.owner, friend.getOwner())
                .build().findFirst();

        if (old != null) {
            friend.setId(old.getId());
        } else {
            friend.setId(0);
        }
        friendBox.put(friend);
    }

    @Override
    public void save(Friend... friends) {
        if (friends.length == 0) {
            return;
        }
        for (Friend friend : friends) {
            save(friend);
        }
    }

    @Override
    public void update(Friend friend) {
        if (friend == null) {
            return;
        }
        friendBox.put(friend);
    }

    @Override
    public void delete(Friend friend) {
        if (friend == null) {
            return;
        }
        if (friend.groupCount > 0) {
            friend.setStatus(Friend.status_deleted);
            friendBox.put(friend);
            return;
        }
        List<Friend> friends = loadLocal(friend.getOwner(), friend.account);
        friendBox.remove(friends);
    }

    @Override
    public void subGroupCount(Friend friend) {
        if (friend == null) {
            return;
        }
        int groupCount = friend.getGroupCount();
        if (groupCount < 2 && StringUtils.equal(friend.status, Friend.status_deleted)) {
            friendBox.remove(friend);
        } else {
            friend.setGroupCount(groupCount - 1);
            friendBox.put(friend);
        }
    }

    @Override
    public void addSystemFriendLocal(String owner) {
        Friend system = get(owner, Friend.system);
        if (system == null) {
            Friend friend = new Friend();
            friend.setStatus(Friend.status_waiting);
            friend.setAccount(Friend.system);
            friend.setOwner(owner);
            friendBox.put(friend);
        }

    }
}
