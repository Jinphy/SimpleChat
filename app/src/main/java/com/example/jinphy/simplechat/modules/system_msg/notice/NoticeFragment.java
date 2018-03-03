package com.example.jinphy.simplechat.modules.system_msg.notice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy, on 2018/3/2, at 10:09
 */
public class NoticeFragment extends BaseFragment<NoticePresenter> implements NoticeContract.View {
    private RecyclerView recyclerView;
    private NoticeRecyclerViewAdapter adapter;

    public NoticeFragment() {
        // Required empty public constructor
    }
    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
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
        return R.layout.fragment_notice;
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = presenter.getAdapter();
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() > 0) {
            ((NoticeActivity) activity()).updateTitle(adapter.getItemCount());
        }
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
        Message message = (Message) item;
        switch (view.getId()) {
            case R.id.item_view:
                break;
        }
    }


}