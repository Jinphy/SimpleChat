package com.example.jinphy.simplechat.modules.main.friends;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.event_bus.EBFriend;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateFriend;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends BaseFragment<FriendsPresenter> implements FriendsContract.View {

    RecyclerView recyclerView;

    FloatingActionButton fab;
    private FriendsRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private View root = null;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void initFab(Activity activity) {
        this.fab = activity.findViewById(R.id.fab);
        this.fab.setTranslationY(0);
        this.fab.setVisibility(View.GONE);
        this.fab.setImageResource(R.drawable.ic_arrow_up_24dp);
        this.fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void fabAction(View view) {
        ((MainFragment) getParentFragment()).showBar(recyclerView);
        recyclerView.smoothScrollToPosition(0);
    }



    private RecyclerView.OnScrollListener getOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            int total = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy>0时，向上滑动，反之向下
                total+=dy;
                if (total > 300) {
                    total=0;
                    ((MainFragment) getParentFragment()).hideBar(recyclerView);
                }
                if (total < -300) {
                    total=0;
                    ((MainFragment) getParentFragment()).showBar(recyclerView);
                }
            }
        };
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_friends;
    }

    @Override
    public void initData() {
    }

    @Override
    protected void findViewsById(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = super.onCreateView(inflater, container, savedInstanceState);
        }
        return root;
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FriendsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        presenter.loadFriends(activity());
    }

    @Override
    protected void registerEvent() {
        recyclerView.addOnScrollListener(getOnScrollListener());
        adapter.onClick(this::handleItemEvent);
    }

    public <T>void handleItemEvent(View view, T item,int type,int position) {
        Friend friend = (Friend) item;
        switch (view.getId()) {
            case R.id.item_view:
                ModifyFriendInfoActivity.start(activity(), friend.getAccount());
                break;
        }
    }


    @Override
    protected FriendsPresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getFriendsPresenter(this);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void updateFriends(List<Friend> friends) {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.clear();
        adapter.update(friends);
        if (i >= 0 && i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadAvatar(EBFriend msg) {
        presenter.loadAvatar(activity(), msg.data);
    }

    @Override
    public void updateView() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.notifyDataSetChanged();
        if (i >= 0 && i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFriend(EBUpdateView msg) {
        LogUtils.e("friend");
        presenter.loadFriends(activity());
    }
}
