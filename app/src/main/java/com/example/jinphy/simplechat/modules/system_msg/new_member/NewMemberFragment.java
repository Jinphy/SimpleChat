package com.example.jinphy.simplechat.modules.system_msg.new_member;

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
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.system_msg.notice.NoticeActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy, on 2018/3/12, at 21:59
 */
public class NewMemberFragment extends BaseFragment<NewMemberPresenter> implements NewMemberContract.View {


    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter<NewMember> adapter;

    public NewMemberFragment() {
        // Required empty public constructor
    }

    /**
     * DESC:
     * Created by jinphy, on 2018/3/12, at 22:00
     */
    public static NewMemberFragment newInstance() {
        NewMemberFragment fragment = new NewMemberFragment();
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
        adapter.forEach(item -> {
            if (item.isNew()) {
                item.message.setNew(false);
                msg.add(item.message);
            }
        });

        if (msg.size() > 0) {
            presenter.updateMsg(msg);
        }
        EventBus.getDefault().post(new EBInteger(2));
    }
    @Override
    protected int getResourceId() {
        return R.layout.fragment_new_member;
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

        adapter = MyAdapter.<NewMember>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_new_member_item)
                .data(presenter.loadNewMembers())
                .onCreateView(holder -> {
                    holder.circleImageViews(R.id.avatar_view);
                    holder.textViews(
                            R.id.name_view,                     // 0
                            R.id.group_no_view,                 // 1
                            R.id.time_view,                     // 2
                            R.id.account_view,                  // 3
                            R.id.extra_msg_view,                // 4
                            R.id.btn_reject,                    // 5
                            R.id.btn_agree                      // 6
                    );
                    holder.views(
                            R.id.new_msg_view,                  // 0
                            R.id.status_ok_view,                // 1
                            R.id.status_no_view,                // 2
                            R.id.status_invalidate_view,        // 3
                            R.id.btn_head                       // 4
                    );
                })
                .onBindView((holder, item, position) -> {
                    Bitmap avatar = item.getAvatar();
                    if (avatar == null) {
                        holder.circleImageView[0].setImageResource(R.drawable
                                .ic_group_chat_white_24dp);
                    } else {
                        holder.circleImageView[0].setImageBitmap(avatar);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.textView[1].setText(item.getGroupNo());
                    holder.textView[2].setText(item.getTime());
                    holder.textView[3].setText(item.getAccount());
                    holder.textView[4].setText(item.getExtraMsg());
                    holder.view[0].setVisibility(item.isNew() ? View.VISIBLE : View.GONE);

                    switch (item.getStatus()) {
                        case Group.STATUS_OK:
                            holder.view[1].setVisibility(View.VISIBLE);
                            holder.view[2].setVisibility(View.GONE);
                            holder.view[3].setVisibility(View.GONE);
                            holder.textView[5].setVisibility(View.GONE);
                            holder.textView[6].setVisibility(View.GONE);
                            break;
                        case Group.STATUS_NO:
                            holder.view[1].setVisibility(View.GONE);         // status_ok
                            holder.view[2].setVisibility(View.VISIBLE);      // status_no
                            holder.view[3].setVisibility(View.GONE);         // status_invalidate
                            holder.textView[5].setVisibility(View.GONE);     // btnReject
                            holder.textView[6].setVisibility(View.GONE);     // btnAgree
                            break;
                        case Group.STATUS_WAITING:
                            holder.view[1].setVisibility(View.GONE);         // status_ok
                            holder.view[2].setVisibility(View.GONE);         // status_no
                            holder.view[3].setVisibility(View.GONE);         // status_invalidate
                            holder.textView[5].setVisibility(View.VISIBLE);  // btnReject
                            holder.textView[6].setVisibility(View.VISIBLE);  // btnAgree
                            holder.textView[5].setEnabled(true);
                            holder.textView[6].setEnabled(true);
                            holder.textView[5].setTextColor(0xffD50000);
                            holder.textView[6].setTextColor(0xff558B2F);
                            break;
                        case Group.STATUS_INVALIDATE:
                            holder.view[1].setVisibility(View.GONE);          // status_ok
                            holder.view[2].setVisibility(View.GONE);          // status_no
                            holder.view[3].setVisibility(View.VISIBLE);       // status_invalidate
                            holder.textView[5].setVisibility(View.VISIBLE);   // btnReject
                            holder.textView[6].setVisibility(View.VISIBLE);   // btnAgree
                            holder.textView[5].setEnabled(false);
                            holder.textView[6].setEnabled(false);
                            holder.textView[5].setTextColor(0x7faaaaaa);
                            holder.textView[6].setTextColor(0x7faaaaaa);
                        default:
                            break;
                    }

                    // btnHead、btnReject、btnAgree
                    holder.setClickedViews(holder.view[4], holder.textView[5], holder.textView[6]);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (v.getId()) {
                        case R.id.btn_head:
                            ModifyGroupActivity.start(activity(), item.getGroupNo());
                            break;
                        case R.id.btn_reject:
                            item.reject();
                            presenter.updateMsg(item.message);
                            int i = linearLayoutManager.findFirstVisibleItemPosition();
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(i);
                            break;
                        case R.id.btn_agree:
                            presenter.agreeJoinGroup(activity(), item);
                            break;
                        default:
                            break;

                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    MyMenu.create(activity())
                            .width(200)
                            .item("删除", (menu, item1) -> {
                                presenter.deleteMsg(item.message);
                                EventBus.getDefault().post(new EBInteger(true, 2));
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
        if (adapter.getItemCount() > 0) {
            ((NewMemberActivity) activity()).updateTitle(adapter.getItemCount());
        }
        setTitle();
        setupEmptyView();
    }

    private void setTitle() {
        NewMemberActivity activity = (NewMemberActivity) activity();
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

    @Override
    public void whenAgreeOk(NewMember newMember) {
        App.showToast("您已同意该成员加入群聊！", false);
        newMember.agree();
        int scrollY = recyclerView.getScrollY();
        presenter.updateMsg(newMember.message);
        adapter.update(presenter.loadNewMembers());
        adapter.notifyDataSetChanged();
        recyclerView.scrollBy(0, scrollY);
        setupEmptyView();
    }
}
