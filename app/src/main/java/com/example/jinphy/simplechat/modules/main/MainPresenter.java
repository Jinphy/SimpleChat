package com.example.jinphy.simplechat.modules.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.models.event_bus.EBIntent;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainPresenter implements MainContract.Presenter {

    private final WeakReference<Context> context;
    private MainContract.View view;

    private UserRepository userRepository;
    private FriendRepository friendRepository;
    private GroupRepository groupRepository;
    private MemberRepository memberRepository;

    private int readTimeout = 15_000;// 15秒


    public MainPresenter(Context context, MainContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.context = new WeakReference<>(context);
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public void findUser(String account, BaseRepository.OnDataOk<Response<User>> callback) {
        if (ObjectHelper.reference(context)) {
            Context context = this.context.get();

            User user = userRepository.currentUser();
            if (StringUtils.equal(user.getAccount(), account)) {
                view.showUserInfo();
                return;
            }
            Friend friend = FriendRepository.getInstance().get(user.getAccount(), account);
            if (friend != null) {
                switch (friend.getStatus()) {
                    case Friend.status_ok:
                    case Friend.status_black_listing:
                    case Friend.status_black_listed:
                        view.showFriendInfo(account);
                        return;
                }
            }

            userRepository.<User>newTask()
                    .param(Api.Key.account, account)
                    .doOnDataOk(callback::call)
                    .doOnDataNo(callback::call)
                    .submit(task -> userRepository.findUser(context, task));
        }
    }

    @Override
    public void findGroups(String text, Runnable whenDataOk) {
        if (ObjectHelper.reference(context) && !TextUtils.isEmpty(text)) {
            Context context = this.context.get();
            groupRepository.<List<Map<String,String>>>newTask()
                    .param(Api.Key.text, text)
                    .autoShowNo(false)
                    .doOnDataOk(okData -> {
                        List<Group> groups = Group.parse(okData.getData());
                        Group.setOwner(groups,userRepository.currentUser().getAccount());
                        groupRepository.saveSearch(groups);
                        if (whenDataOk != null) {
                            whenDataOk.run();
                        }
                        String[] groupNos = new String[groups.size()];
                        int i = 0;
                        for (Group group : groups) {
                            groupNos[i++] = group.getGroupNo();
                        }
                        loadAvatars(groupNos);
                    })
                    .doOnDataNo(reason -> {
                        if (BaseRepository.TYPE_CODE.equals(reason)) {
                            App.showToast("没有找到相应的群！", false);
                        } else {
                            App.showToast("查询异常，请稍后再试！", false);
                        }
                    })
                    .submit(task -> groupRepository.search(context, task));
        }
    }

    @Override
    public void checkAccount(Context context) {
        userRepository.checkAccount(context);
    }

    //------------------------------------------------------------------------
    @Override
    public void loadDataAfterLogin() {
        if (ObjectHelper.reference(context)) {
            String owner = userRepository.currentUser().getAccount();
            loadFriends(context.get(), owner);
        }
    }

    /**
     * DESC: 加载好友
     * Created by jinphy, on 2018/3/11, at 15:21
     */
    private void loadFriends(Context context, String owner) {
        if (context == null) {
            return;
        }
        friendRepository.<List<Map<String, String>>>newTask()
                .param(Api.Key.owner, owner)
                .autoShowNo(false)
                .readTimeout(readTimeout)
                .doOnDataOk(friendsData -> {
                    LogUtils.e("加载好友成功！");
                    List<Friend> friends = Friend.parse(friendsData.getData());
                    friendRepository.save(friends);
                    loadGroups(context, owner);
                    loadFriendAvatars(friends);
                    EventBus.getDefault().post(new EBUpdateView());
                })
                .submit(task -> friendRepository.loadOnline(context, task));
    }

    /**
     * DESC: 加载头像
     * Created by jinphy, on 2018/3/11, at 16:36
     */
    private void loadAvatars(String[] accounts) {
        if (accounts == null || accounts.length == 0) {
            return;
        }

        int totalCount = accounts.length;
        EBInteger counter = new EBInteger(0);
        for (String account : accounts) {
            userRepository.<Map<String, String>>newTask()
                    .param(Api.Key.account, account)
                    .showProgress(false)
                    .autoShowNo(false)
                    .readTimeout(readTimeout)
                    .doOnDataOk(avatarData -> {
                        Map<String, String> data = avatarData.getData();
                        String avatar = data.get(User_.avatar.name);
                        if (!TextUtils.isEmpty(avatar)) {
                            Bitmap bitmap = StringUtils.base64ToBitmap(avatar);
                            ImageUtil.storeAvatar(account, bitmap);
                        }
                    })
                    .doOnFinal(()->{
                        counter.add();
                        if (counter.data == totalCount) {
                            EventBus.getDefault().post(new EBUpdateView());
                        }
                    })
                    .submit(task -> userRepository.getAvatar(null, task));

        }

    }

    /**
     * DESC: 加载好友头像
     * Created by jinphy, on 2018/3/11, at 16:02
     */
    private void loadFriendAvatars(List<Friend> friends) {
        if (friends != null && friends.size() > 0) {
            String[] accounts = new String[friends.size()];
            int i = 0;
            for (Friend friend : friends) {
                accounts[i++] = friend.getAccount();
            }
            loadAvatars(accounts);
        }
    }

    /**
     * DESC: 加载群头像
     * Created by jinphy, on 2018/3/11, at 16:35
     */
    private void loadGroupAvatars(List<Group> groups) {
        if (groups != null && groups.size() > 0) {
            String[] groupNos = new String[groups.size()];
            int i = 0;
            for (Group friend : groups) {
                groupNos[i++] = friend.getGroupNo();
            }
            loadAvatars(groupNos);
        }
    }

    /**
     * DESC: 加载群聊
     * Created by jinphy, on 2018/3/11, at 15:27
     */
    private void loadGroups(Context context, String owner) {
        if (context == null) {
            return;
        }
        groupRepository.<List<Map<String, String>>>newTask()
                .param(Api.Key.owner, owner)
                .autoShowNo(false)
                .readTimeout(readTimeout)
                .doOnDataOk(groupsData -> {
                    LogUtils.e("加载群聊成功！");
                    List<Group> groups = Group.parse(groupsData.getData());
                    groupRepository.saveMyGroup(groups);
                    EventBus.getDefault().post(new EBUpdateView());
                    loadMembers(context, groups);
                    loadGroupAvatars(groups);
                })
                .submit(task -> groupRepository.loadOnline(context, task));
    }

    /**
     * DESC: 加载群成员
     *
     * Created by jinphy, on 2018/3/11, at 15:27
     */
    private void loadMembers(Context context, List<Group> groups) {
        if (context != null && groups != null && groups.size() > 0) {
            String[] groupNos = new String[groups.size()];
            int i = 0;
            for (Group group : groups) {
                groupNos[i++] = group.getGroupNo();
            }
            memberRepository.<List<Map<String, String>>>newTask()
                    .param(Api.Key.groupNos, GsonUtils.toJson(groupNos))
                    .autoShowNo(false)
                    .readTimeout(readTimeout)
                    .doOnDataOk(membersData -> {
                        List<Member> members = Member.parse(membersData.getData());
                        memberRepository.save(members);
                        EventBus.getDefault().post(new EBUpdateView());
                    })
                    .submit(task -> memberRepository.loadOnline(context, task));
        }
    }

    @Override
    public Group getGroup(String groupNo) {
        return groupRepository.get(groupNo, userRepository.currentUser().getAccount());
    }
}
