package com.example.jinphy.simplechat.modules.group.group_list;

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
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * DESC: 群列表
 * Created by jinphy, on 2018/3/11, at 9:08
 */
public class GroupListFragment extends BaseFragment<GroupListPresenter> implements GroupListContract.View {

    public static final String SHOW_SEARCH_RESULT = "SHOW_SEARCH_RESULT";



    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayoutManager linearLayoutManager;
    private GroupListAdapter adapter;
    private boolean showSearchResult = false;

    public GroupListFragment() {
        // Required emptyView public constructor
    }

    public static GroupListFragment newInstance(boolean showSearchResult) {
        GroupListFragment fragment = new GroupListFragment();
        fragment.showSearchResult = showSearchResult;
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
        if (showSearchResult) {
            presenter.removeSearchedResult();
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_group_list;
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
        adapter = new GroupListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadGroups(showSearchResult));
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
            Group group = (Group) item;
            switch (view.getId()) {
                case R.id.qr_code_view:// qRCodeView

                    // TODO: 2018/3/11 展示二维码
                    break;
                default:// itemView
                    ModifyGroupActivity.start(activity(), group.getGroupNo());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        adapter.update(presenter.loadGroups(showSearchResult));
        App.showToast("群聊信息已更新", false);
        setupEmptyView();
    }


}
