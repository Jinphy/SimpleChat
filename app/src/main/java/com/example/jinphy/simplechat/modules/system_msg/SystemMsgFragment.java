package com.example.jinphy.simplechat.modules.system_msg;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendsActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberActivity;
import com.example.jinphy.simplechat.modules.system_msg.notice.NoticeActivity;

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
        itemNotice = view.findViewById(R.id.item_notice);
        itemNewMember = view.findViewById(R.id.item_new_member);

        newFriendView = view.findViewById(R.id.new_friend_view);
        newMemberView = view.findViewById(R.id.new_member_view);
        noticeView = view.findViewById(R.id.new_notice_view);

        titleNewFriend = view.findViewById(R.id.title_new_friend);
        titleNewMember = view.findViewById(R.id.title_new_member);
        titleNotice = view.findViewById(R.id.title_notice);
    }

    @Override
    protected void setupViews() {
        int newFriendCount = presenter.countFriends();
        int newMemberCount = presenter.countMembers();
        int noticeCount = presenter.countNotices();
        if (newFriendCount > 0) {
            titleNewFriend.setText("新朋友（" + newFriendCount + "）");
        }
        if (newMemberCount > 0) {
            titleNewMember.setText("新成员（" + newMemberCount + "）");
        }
        if (noticeCount > 0) {
            titleNotice.setText("公告（" + noticeCount + ")");
        }

        newFriendView.setVisibility(presenter.countNewFriends() > 0 ? View.VISIBLE : View.GONE);
        newMemberView.setVisibility(presenter.countNewMembers() > 0 ? View.VISIBLE : View.GONE);
        noticeView.setVisibility(presenter.countNewNotices() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void registerEvent() {
        itemNewFriend.setOnClickListener(v->{
            NewFriendsActivity.start(activity());
        });

        itemNotice.setOnClickListener(v -> {
            NoticeActivity.start(activity());
        });
        itemNewMember.setOnClickListener(v -> {
            NewMemberActivity.start(activity());
        });
    }



    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_chat_fragment,menu);
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
                break;
            case 2:
                newMemberView.setVisibility(View.GONE);
                break;
            case 3:
                noticeView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
