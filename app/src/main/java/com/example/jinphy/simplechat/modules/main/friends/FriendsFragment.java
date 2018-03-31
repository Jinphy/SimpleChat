package com.example.jinphy.simplechat.modules.main.friends;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.event_bus.EBFriend;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateFriend;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

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
    View emptyView;

    FloatingActionButton fab;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter<Friend> adapter;

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
        emptyView = view.findViewById(R.id.empty_view);
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
        adapter = MyAdapter.<Friend>newInstance()
                .onInflate(viewType -> R.layout.main_tab_friends_item)
                .data(presenter.loadFriends())
                .onCreateView(holder -> {
                    // avatar
                    holder.circleImageViews(R.id.avatar);
                    // remark、account、address、date
                    holder.textViews(R.id.remark, R.id.account, R.id.address, R.id.date);
                })
                .onBindView((holder, item, position) -> {
                    // 设置头像
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 50, 50);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    }
                    // 设置备注
                    holder.textView[0].setText(item.getRemark());
                    // 设置账号
                    holder.textView[1].setText(item.getAccount());
                    // 设置地址
                    holder.textView[2].setText(item.getAddress());
                    // 设置时间
                    if (!TextUtils.isEmpty(item.getDate())) {
                        holder.textView[3].setText(StringUtils.formatDate(Long.valueOf(item
                                .getDate())));
                    }

                    // 设置需要监听点击事件的view
                    holder.setClickedViews(holder.item);

                    // 设置需要监听长按事件的view
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    ModifyFriendInfoActivity.start(activity(), item.getAccount());
                })
                .onLongClick((v, item, holder, type, position) -> false)
                .into(recyclerView);

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
    public void updateView() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.notifyDataSetChanged();
        if (i >= 0 && i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
        setupEmptyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFriend(EBUpdateView msg) {
        adapter.update(presenter.loadFriends());
        setupEmptyView();
    }
}
