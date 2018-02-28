package com.example.jinphy.simplechat.models.friend;

import android.content.Context;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiCallback;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

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
    public List<Friend> loadLocal(String owner) {
        return friendBox.query()
                .equal(Friend_.owner, owner)
                .and()
                .equal(Friend_.status, Friend.status_ok)
                .build().find();
    }

    /**
     * DESC: 从服务器中获取好友列表
     * Created by jinphy, on 2018/2/28, at 18:29
     */
    @Override
    public void loadOnline(Context context, Task<List<Friend>> task) {
        Api.<List<Friend>>common(context)
                .hint("正在加载...")
                .path(Api.Path.loadFriends)
                .cancellable(false)
                .dataType(Api.Data.MODEL_LIST,Friend.class)
                .setup(api -> this.handleBuilder(api, task))
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
        // TODO: 2018/1/18

        return null;
    }

    @Override
    public void addFriend(Context context, Task<Map<String, String>> task) {
        Api.<Map<String, String>>common(context)
                .hint("正在申请...")
                .path(Api.Path.addFriend)
                .dataType(Api.Data.MAP)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    @Override
    public void save(List<Friend> friends) {
        if (friends == null) {
            return;
        }
        User user = UserRepository.getInstance().currentUser();
        for (Friend friend : friends) {
            friend.setUser(user);
            friend.setId(0);
        }
        friendBox.removeAll();
        friendBox.put(friends);
    }

    @Override
    protected <T> void handleBuilder(ApiInterface<Response<T>> api, Task<T> task) {
        api.showProgress(task.isShowProgress())
                .useCache(task.isUseCache())
                .autoShowNo(task.isAutoShowNo())
                .params(task.getParams())
                .onResponseYes(task.getOnDataOk()==null?null: response -> task.getOnDataOk().call(response))
                .onResponseNo(task.getOnDataNo()==null?null: response -> task.getOnDataNo().call(TYPE_CODE))
                .onError(task.getOnDataNo()==null?null: e-> task.getOnDataNo().call(TYPE_ERROR))
                .onFinal(task.getOnFinal()==null?null: task.getOnFinal()::call);
    }

}
