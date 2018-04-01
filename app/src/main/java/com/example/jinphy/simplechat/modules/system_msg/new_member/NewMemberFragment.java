package com.example.jinphy.simplechat.modules.system_msg.new_member;

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
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberAdapter.NewMember;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * DESC:
 * Created by jinphy, on 2018/3/12, at 21:59
 */
public class NewMemberFragment extends BaseFragment<NewMemberPresenter> implements NewMemberContract.View {


    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayoutManager linearLayoutManager;
    private NewMemberAdapter adapter;

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
        List<Message> msg = adapter.getNewMsgAndSetOld();
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

        adapter = new NewMemberAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadNewMembers());
        if (adapter.getItemCount() > 0) {
            ((NewMemberActivity) activity()).updateTitle(adapter.getItemCount());
        }
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
        adapter.onClick((view, item, type, position) -> {
            NewMember newMember = (NewMember) item;
            switch (view.getId()) {
                case R.id.btn_head:
                    ModifyGroupActivity.start(activity(), newMember.getGroupNo());
                    break;
                case R.id.btn_reject:
                    newMember.reject();
                    presenter.updateMsg(newMember.message);
                    int i = linearLayoutManager.findFirstVisibleItemPosition();
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(i);
                    break;
                case R.id.btn_agree:
                    presenter.agreeJoinGroup(activity(), newMember);
                    break;
                default:
                    break;

            }
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

    @Override
    public void whenAgreeOk(NewMember newMember) {
        App.showToast("您已同意该成员加入群聊！", false);
        newMember.agree();
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        presenter.updateMsg(newMember.message);
        adapter.update(presenter.loadNewMembers());
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(i);
        setupEmptyView();
    }
}
