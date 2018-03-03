package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * DESC:
 * Created by jinphy, on 2018/3/2, at 9:53
 */
public class NewFriendsFragment extends BaseFragment<NewFriendsPresenter> implements NewFriendsContract.View {

    private RecyclerView recyclerView;
    private NewFriendRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    public NewFriendsFragment() {
        // Required empty public constructor
    }

    public static NewFriendsFragment newInstance() {
        NewFriendsFragment fragment = new NewFriendsFragment();
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
        return R.layout.fragment_new_friends;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void findViewsById(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NewFriendRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadNewFriends());

    }

    @Override
    protected void registerEvent() {
        adapter.onClick(this::handleItemEvent);
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


    public <T>void handleItemEvent(View view, T item,int type,int position) {
        NewFriendRecyclerViewAdapter.NewFriend newFriend = (NewFriendRecyclerViewAdapter.NewFriend) item;
        switch (view.getId()) {
            case R.id.item_view:
                String account = newFriend.friend.getAccount();
                ModifyFriendInfoActivity.start(activity(), account);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.clear();
        adapter.update(presenter.loadNewFriends());
        if (i >= 0 && i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
    }

}
