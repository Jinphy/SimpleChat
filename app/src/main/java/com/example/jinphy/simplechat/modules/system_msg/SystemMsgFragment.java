package com.example.jinphy.simplechat.modules.system_msg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_view.menu.MyMenu;
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberActivity;
import com.example.jinphy.simplechat.modules.system_msg.notice.NoticeActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 *
 *
 */
public class SystemMsgFragment extends BaseFragment<SystemMsgPresenter> implements SystemMsgContract.View{

    private View itemNewFriend;
    private View itemNewMember;
    private View itemNotice;

    private View newFriendView;
    private View newMemberView;
    private View noticeView;

    private TextView titleNewFriend;
    private TextView titleNewMember;
    private TextView titleNotice;
    private int newFriendCount;
    private int newMemberCount;
    private int noticeCount;

    public SystemMsgFragment() {
        // Required empty public constructor
    }

    public static SystemMsgFragment newInstance() {
        SystemMsgFragment fragment = new SystemMsgFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }

    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_system_msg;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        itemNewFriend = view.findViewById(R.id.item_new_friend);
        itemNewMember = view.findViewById(R.id.item_new_member);
        itemNotice = view.findViewById(R.id.item_notice);

        newFriendView = view.findViewById(R.id.new_friend_view);
        newMemberView = view.findViewById(R.id.new_member_view);
        noticeView = view.findViewById(R.id.new_notice_view);

        titleNewFriend = view.findViewById(R.id.title_new_friend);
        titleNewMember = view.findViewById(R.id.title_new_member);
        titleNotice = view.findViewById(R.id.title_notice);
    }

    @Override
    protected void setupViews() {
        newFriendCount = presenter.countFriends();
        newMemberCount = presenter.countMembers();
        noticeCount = presenter.countNotices();
        setTitle(titleNewFriend, "新朋友", newFriendCount);
        setTitle(titleNewMember, "新成员", newMemberCount);
        setTitle(titleNotice, "公告", noticeCount);
        newFriendView.setVisibility(presenter.countNewFriends() > 0 ? View.VISIBLE : View.GONE);
        newMemberView.setVisibility(presenter.countNewMembers() > 0 ? View.VISIBLE : View.GONE);
        noticeView.setVisibility(presenter.countNewNotices() > 0 ? View.VISIBLE : View.GONE);
    }

    private void setTitle(TextView titleView, String title, int count) {
        if (count == 0) {
            titleView.setText(title);
        } else {
            titleView.setText(String.format("%s%s%d%s", title, "(", count, ")"));
        }
    }

    @Override
    protected void registerEvent() {
        itemNewFriend.setOnClickListener(v->{
            NewFriendsActivity.start(activity());
        });
        itemNewMember.setOnClickListener(v -> {
            NewMemberActivity.start(activity());
        });
        itemNotice.setOnClickListener(v -> {
            NoticeActivity.start(activity());
        });

        itemNewFriend.setOnLongClickListener(this::showMenu);
        itemNewMember.setOnLongClickListener(this::showMenu);
        itemNotice.setOnLongClickListener(this::showMenu);
    }


    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main_fragment,menu);
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean showMenu(View view) {
        MyMenu.create(activity())
                .width(200)
                .item("清空", (menu, item) -> {
                    switch (view.getId()) {
                        case R.id.item_new_friend:
                            if (newFriendCount == 0) {
                                break;
                            }
                            presenter.deleteMsg(Message.TYPE_SYSTEM_ADD_FRIEND);
                            titleNewFriend.setText("新朋友");
                            break;
                        case R.id.item_new_member:
                            if (newMemberCount == 0) {
                                break;
                            }
                            presenter.deleteMsg(Message.TYPE_SYSTEM_APPLY_JOIN_GROUP);
                            titleNewMember.setText("新成员");
                            break;
                        case R.id.item_notice:
                            if (noticeCount == 0) {
                                break;
                            }
                            presenter.deleteMsg(Message.TYPE_SYSTEM_NOTICE);
                            titleNotice.setText("公告");
                            break;
                        default:
                            break;
                    }
                    App.showToast("信息已清空!", false);
                    EventBus.getDefault().post(new EBUpdateView());
                })
                .display();

        return true;
    }


    @Override
    public boolean onBackPressed() {
        finishActivity();
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBInteger msg) {
        switch (msg.data) {
            case 1:
                newFriendView.setVisibility(View.GONE);
                if (msg.ok) {
                    setTitle(titleNewFriend, "新朋友", --newFriendCount);
                }
                break;
            case 2:
                newMemberView.setVisibility(View.GONE);
                if (msg.ok) {
                    setTitle(titleNewMember, "新成员", --newMemberCount);
                }
                break;
            case 3:
                noticeView.setVisibility(View.GONE);
                if (msg.ok) {
                    setTitle(titleNotice, "公告", --noticeCount);
                }
                break;
            default:
                break;
        }
        presenter.updateSystemMsgRecord();
        EventBus.getDefault().post(new EBUpdateView());
    }
}
