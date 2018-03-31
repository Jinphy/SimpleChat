package com.example.jinphy.simplechat.modules.main.msg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.custom_view.menu.MyMenu;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.chat.ChatFragment;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;

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
    private MyAdapter<MessageRecord> adapter;

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

        adapter = MyAdapter.<MessageRecord>newInstance()
                .onInflate(viewType -> R.layout.main_tab_msg_item)
                .data(presenter.loadMsgRecords())
                .onCreateView(holder -> {
                    // avatar 头像
                    holder.circleImageViews(R.id.avatar);
                    // name 昵称、lastMsg 最新消息、time 最新消息的时间、count 未读消息数
                    holder.textViews(R.id.name, R.id.last_msg, R.id.time, R.id.new_count);
                    // to top 置顶
                    holder.views(R.id.top);
                })
                .onBindView((holder, item, position) -> {
                    // 设置头像
                    if (Friend.system.equals(item.getWith())) {
                        holder.circleImageView[0].setImageResource(R.drawable.ic_system_24dp);
                    } else {
                        Bitmap bitmap = ImageUtil.loadAvatar(item.getWith(), 30, 30);
                        if (bitmap != null) {
                            holder.circleImageView[0].setImageBitmap(bitmap);
                        } else if (item.getWith().contains("G")) {
                            holder.circleImageView[0].setImageResource(R.drawable
                                    .ic_group_chat_white_24dp);
                        } else {
                            holder.circleImageView[0].setImageResource(R.drawable.ic_person_48dp);
                        }
                    }
                    // 设置昵称
                    holder.textView[0].setText(item.getName());
                    // 设置最新消息
                    holder.textView[1].setText(item.getContent());
                    // 设置时间
                    holder.textView[2].setText(item.getTime());
                    // 设置未读消息数
                    int count = item.getNewMsgCount();
                    if (count == 0) {
                        holder.textView[3].setVisibility(View.GONE);
                    } else {
                        holder.textView[3].setVisibility(View.VISIBLE);
                        if (count < 100) {
                            holder.textView[3].setText(count + "");
                        } else {
                            holder.textView[3].setText("99+");
                        }
                    }
                    // 设置是否有置顶
                    holder.view[0].setVisibility(item.getToTop() == 1 ? View.VISIBLE : View.GONE);

                    // 设置需要监听点击的view
                    holder.setClickedViews(holder.item);

                    // 设置需要监听长按的view
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    String with = item.getWith();
                    if (Friend.system.equals(with)) {
                        SystemMsgActivity.start(activity());
                    } else {
                        showChatWindow(item);
                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    MyMenu.create(activity())
                            .width(200)
                            .item(item.isToTop() ? "取消置顶" : "置顶", (menu, item1) -> {
                                int scrollY = recyclerView.getScrollY();
                                item.updateToTop();
                                presenter.updateRecord(item);
                                MessageRecord.sort(adapter.getData());
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollBy(0, scrollY);
                                App.showToast(item.isToTop() ? "已置顶该聊天！" : "置顶已取消！", false);
                            })
                            .item("删除", (menu, item12) -> {
                                presenter.deleteMsgRecord(item);
                                int scrollY = recyclerView.getScrollY();
                                adapter.remove(position);
                                recyclerView.scrollBy(0, scrollY);
                                App.showToast("已删除该聊天！", false);
                            })
                            .item("清空", (menu, item13) -> {
                                presenter.clearMsg(item);
                                adapter.update(presenter.loadMsgRecords());
                                int scrollY = recyclerView.getScrollY();
                                recyclerView.scrollBy(0, scrollY);
                                App.showToast("已清空该聊天！", false);
                            })
                            .display();
                    return true;
                })
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
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void showChatWindow(MessageRecord record) {
        ChatActivity.start(activity(), record.getWith());
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
}
