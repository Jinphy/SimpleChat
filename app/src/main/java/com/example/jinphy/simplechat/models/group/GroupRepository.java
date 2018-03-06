package com.example.jinphy.simplechat.models.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.event_bus.EBBase;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;
import java.util.Map;

import io.objectbox.Box;

/**
 *
 * Created by Jinphy on 2018/3/5.
 */

public class GroupRepository extends BaseRepository implements GroupDataSource  {

    private Box<Group> groupBox;


    private static class InstanceHolder{
        static final GroupRepository DEFAULT = new GroupRepository();
    }

    public static GroupRepository getInstance() {
        return GroupRepository.InstanceHolder.DEFAULT;
    }

    public GroupRepository() {
        groupBox = App.boxStore().boxFor(Group.class);
    }


    @Override
    public List<Group> loadLocal(String owner) {
        return groupBox.query()
                .equal(Group_.owner, owner)
                .build().find();
    }

    @Override
    public void save(Group... groups) {
        if (groups.length == 0) {
            return;
        }
        for (Group group : groups) {
            Group old = get(group.groupNo, group.owner);
            if (old != null) {
                group.setId(old.getId());
            } else {
                group.setId(0);
            }
        }
        groupBox.put(groups);
    }

    @Override
    public Group get(String groupNo, String owner) {
        return groupBox.query()
                .equal(Group_.groupNo, groupNo)
                .equal(Group_.owner, owner)
                .build().findFirst();
    }

    @Override
    public void loadOnline(Context context, Task<List<Map<String, String>>> task) {
        Api.<List<Map<String,String >>>common(context)
                .hint("正在创建...")
                .path(Api.Path.loadGroups)
                .dataType(Api.Data.MAP_LIST)
                .setup(api -> this.handleBuilder(api,task))
                .request();
    }

    @Override
    public void getOnline(Context context, String owner, String groupNo, Runnable... whenOk) {
        EBBase flag = new EBBase<String>(false, null);
        Api.<Map<String, String>>common(context)
                .path(Api.Path.getGroup)
                .param(Api.Key.owner, owner)
                .param(Api.Key.groupNo, groupNo)
                .cancellable(false)
                .showProgress(false)
                .dataType(Api.Data.MAP)
                .onResponseYes(response -> {
                    Group group = Group.parse(response.getData());
                    save(group);
                    synchronized (flag) {
                        if (flag.ok) {
                            if (whenOk.length > 0) {
                                whenOk[0].run();
                                LogUtils.e(1);
                            }
                        } else {
                            flag.ok = true;
                        }
                    }
                })
                .request();
        Api.<Map<String,String>>common(context)
                .path(Api.Path.getAvatar)
                .param(Api.Key.account, groupNo)
                .cancellable(false)
                .showProgress(false)
                .dataType(Api.Data.MAP)
                .onResponseYes(response -> {
                    Map<String, String> data = response.getData();
                    if (!"无".equals(data.get(User_.avatar.name))) {
                        Bitmap bitmap = StringUtils.base64ToBitmap(data.get(User_.avatar.name));
                        ImageUtil.storeAvatar(groupNo, bitmap);
                    }
                    synchronized (flag) {
                        if (flag.ok) {
                            if (whenOk.length > 0) {
                                whenOk[0].run();
                                LogUtils.e(2);
                            }
                        } else {
                            flag.ok = true;
                        }
                    }
                })
                .request();
    }

    @Override
    public void createGroup(Context context, Task<Map<String, String>> task) {
        Api.<Map<String,String >>common(context)
                .hint("正在创建...")
                .path(Api.Path.createGroup)
                .dataType(Api.Data.MAP)
                .setup(api -> this.handleBuilder(api,task))
                .request();
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
