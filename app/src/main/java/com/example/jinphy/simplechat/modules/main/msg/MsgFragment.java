package com.example.jinphy.simplechat.modules.main.msg;

import android.app.Activity;
import android.content.Intent;
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
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateFriend;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.chat.ChatFragment;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MsgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MsgFragment extends BaseFragment<MsgPresenter> implements MsgContract.View{

    private RecyclerView recyclerView;
    private View emptyView;

    private FloatingActionButton fab;
    private MsgRecyclerViewAdapter adapter;

    private View root = null;
    private LinearLayoutManager linearLayoutManager;

    public MsgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static MsgFragment newInstance() {
            MsgFragment fragment = new MsgFragment();
        return fragment;
    }

    @Override
    protected MsgPresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getMsgPresenter(this);
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
        return R.layout.fragment_msg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = super.onCreateView(inflater, container, savedInstanceState);
        }
        return root;
    }


    @Override
    protected void initData() {
    }

    @Override
    protected void findViewsById(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MsgRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadMsgRecords());
        setupEmptyView();
    }

    private void setupEmptyView() {
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void registerEvent() {
        recyclerView.addOnScrollListener(getOnScrollListener());
        adapter.onClick(this::handleItemEvent);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void showChatWindow(MessageRecord record) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatFragment.WITH_ACCOUNT, record.getWith());
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.clear();
        adapter.update(presenter.loadMsgRecords());
        if (i >= 0 && i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
        setupEmptyView();
    }

    public <T>void handleItemEvent(View view, T item,int type,int position) {
        MessageRecord record = (MessageRecord) item;
        switch (view.getId()) {
            case R.id.item_view:
                String with = record.getWith();
                if (Friend.system.equals(with)) {
                    SystemMsgActivity.start(activity());
                } else {
                    showChatWindow(record);
                }
                break;
        }
    }
}
