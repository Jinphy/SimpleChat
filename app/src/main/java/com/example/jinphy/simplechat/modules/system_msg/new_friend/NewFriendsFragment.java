package com.example.jinphy.simplechat.modules.system_msg.new_friend;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.custom_view.menu.MyMenu;
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy, on 2018/3/2, at 9:53
 */
public class NewFriendsFragment extends BaseFragment<NewFriendsPresenter> implements NewFriendsContract.View {

    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter<NewFriend> adapter;


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
    public void onDestroy() {
        super.onDestroy();

        List<Message> msg = new LinkedList<>();
        adapter.forEach(item ->{
            if (item.isNewMsg()) {
                item.message.setNew(false);
                msg.add(item.message);
            }
        });
        if (msg.size() > 0) {
            presenter.updateMsg(msg);
        }
        EventBus.getDefault().post(new EBInteger(1));
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
        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = MyAdapter.<NewFriend>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.main_tab_msg_item)
                .data(presenter.loadNewFriends())
                .onCreateView(holder -> {
                    //avatar
                    holder.circleImageViews(R.id.avatar);
                    // name、time、lastMsg
                    holder.textViews(R.id.name, R.id.time, R.id.last_msg);
                    // newMsgView
                    holder.views(R.id.top);
                })
                .onBindView((holder, item, position) -> {
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 50, 50);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    } else {
                        holder.circleImageView[0].setImageResource(R.drawable.ic_person_48dp);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.textView[1].setText(item.getTime());
                    holder.textView[2].setText(item.getMsg());
                    holder.view[0].setVisibility(item.isNewMsg() ? View.VISIBLE : View.GONE);
                    holder.setClickedViews(holder.item);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (v.getId()) {
                        case R.id.item_view:
                            String account = item.getAccount();
                            ModifyFriendInfoActivity.start(activity(), account);
                            break;
                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    MyMenu.create(activity())
                            .width(200)
                            .item("删除", (menu, item1) -> {
                                presenter.deleteMsg(item.message);
                                EventBus.getDefault().post(new EBInteger(true, 1));
                                App.showToast("已删除！", false);
                                int scrollY = recyclerView.getScrollY();
                                adapter.remove(item);
                                recyclerView.scrollBy(0, scrollY);
                                setTitle();
                            })
                            .display();
                    return true;
                })
                .into(recyclerView);
        setTitle();
        setupEmptyView();
    }

    private void setTitle() {
        NewFriendsActivity activity = (NewFriendsActivity) activity();
        activity.updateTitle(adapter.getItemCount());
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
    public void updateView(EBUpdateView msg) {
        int scrollY = recyclerView.getScrollY();
        adapter.clear();
        adapter.update(presenter.loadNewFriends());
        recyclerView.scrollBy(0, scrollY);
        setupEmptyView();
    }

}
