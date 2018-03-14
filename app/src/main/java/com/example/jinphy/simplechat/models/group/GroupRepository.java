package com.example.jinphy.simplechat.models.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.ApiInterface;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.event_bus.EBBase;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

/**
 * Created by Jinphy on 2018/3/5.
 */

public class GroupRepository extends BaseRepository implements GroupDataSource {

    private Box<Group> groupBox;


    private static class InstanceHolder {
        static final GroupRepository DEFAULT = new GroupRepository();
    }

    public static GroupRepository getInstance() {
        return GroupRepository.InstanceHolder.DEFAULT;
    }

    public GroupRepository() {
        groupBox = App.boxStore().boxFor(Group.class);
    }


    @Override
    public List<Group> loadLocal(String owner, boolean showSearchResult) {
        return groupBox.query()
                .equal(Group_.owner, owner)
                .equal(Group_.isFromSearch, showSearchResult)
                .build().find();
    }

    @Override
    public void saveMyGroup(Group... groups) {
        if (groups.length == 0) {
            return;
        }
        for (Group group : groups) {
            group.isMyGroup = true;
            group.isFromSearch = false;
            Group old = get(group.groupNo, group.owner);
            if (old != null) {
                group.setId(old.getId());
            } else {
                group.setId(0);
            }
            groupBox.put(group);
        }
    }

    public void saveMyGroup(List<Group> groups) {
        if (groups == null || groups.size() == 0) {
            return;
        }
        saveMyGroup(groups.toArray(new Group[groups.size()]));
    }

    @Override
    public void update(Group group) {
        if (group != null) {
            groupBox.put(group);
        }
    }

    @Override
    public void saveSearch(Group... groups) {
        if (groups.length == 0) {
            return;
        }
        for (Group group : groups) {
            Group old = get(group.groupNo, group.owner);
            if (old != null) {
                old.isFromSearch = true;
                groupBox.put(old);
            } else {
                group.isFromSearch = true;
                group.setId(0);
                groupBox.put(group);
            }
        }
    }

    @Override
    public void saveSearch(List<Group> groups) {
        if (groups == null || groups.size() == 0) {
            return;
        }
        saveSearch(groups.toArray(new Group[groups.size()]));
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
        Api.<List<Map<String, String>>>common(context)
                .hint("正在加载...")
                .path(Api.Path.getGroups)
                .dataType(Api.Data.MAP_LIST)
                .setup(api -> this.handleBuilder(api, task))
                .request();
    }

    @Override
    public void search(Context context, Task<List<Map<String, String>>> task) {
        Api.<List<Map<String, String>>>common(context)
                .hint("正在查找...")
                .path(Api.Path.getGroups)
                .dataType(Api.Data.MAP_LIST)
                .setup(api -> this.handleBuilder(api, task))
                .request();

    }

    @Override
    public void deleteSearch() {
        List<Group> groups = groupBox.query()
                .equal(Group_.isFromSearch, true)
                .build().find();
        for (Group group : groups) {
            if (group.isMyGroup) {
                group.isFromSearch = false;
                groupBox.put(group);
            } else {
                groupBox.remove(group);
            }
        }
    }


    public void remove(Group group) {
        groupBox.remove(group);
    }

    @Override
    public void getOnline(Context context, String owner, String groupNo, Runnable... whenOk) {
        EBBase flag = new EBBase<String>(false, null);
        Api.<Map<String, String>>common(context)
                .path(Api.Path.getGroups)
                .param(Api.Key.owner, owner)
                .param(Api.Key.groupNo, groupNo)
                .cancellable(false)
                .showProgress(false)
                .autoShowNo(false)
                .dataType(Api.Data.MAP)
                .onResponseYes(response -> {
                    Group group = Group.parse(response.getData());
                    saveMyGroup(group);
                    synchronized (flag) {
                        if (flag.ok) {
                            if (whenOk.length > 0) {
                                whenOk[0].run();
                            }
                        } else {
                            flag.ok = true;
                        }
                    }
                })
                .request();
        Api.<Map<String, String>>common(context)
                .path(Api.Path.getAvatar)
                .param(Api.Key.account, groupNo)
                .cancellable(false)
                .showProgress(false)
                .autoShowNo(false)
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
        Api.<Map<String, String>>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在创建...")
                .path(Api.Path.createGroup)
                .dataType(Api.Data.MAP)
                .request();
    }

    @Override
    public void modifyGroup(Context context, Task<String> task) {
        Api.<String>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在修改...")
                .path(Api.Path.modifyGroup)
                .dataType(Api.Data.MAP)
                .request();

    }

    @Override
    public void joinGroup(Context context, Task<String> task) {
        Api.<String>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在申请加入...")
                .path(Api.Path.joinGroup)
                .dataType(Api.Data.MAP)
                .request();

    }

    @Override
    public void agreeJoinGroup(Context context, Task<String> task) {
        Api.<String>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在同意加入...")
                .path(Api.Path.agreeJoinGroup)
                .dataType(Api.Data.MAP)
                .request();
    }

    @Override
    public void exitGroup(Context context, Task<String> task) {
        Api.<String>common(context)
                .setup(api -> this.handleBuilder(api, task))
                .hint("正在操作...")
                .path(Api.Path.exitGroup)
                .dataType(Api.Data.MAP)
                .request();

    }
}
