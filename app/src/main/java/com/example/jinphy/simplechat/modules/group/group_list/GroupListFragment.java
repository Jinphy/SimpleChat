package com.example.jinphy.simplechat.modules.group.group_list;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.utils.DialogUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * DESC: 群列表
 * Created by jinphy, on 2018/3/11, at 9:08
 */
public class GroupListFragment extends BaseFragment<GroupListPresenter> implements GroupListContract.View {

    public static final String SAVE_KEY_SHOW_SEARCH_RESULT = "SAVE_KEY_SHOW_SEARCH_RESULT";



    private RecyclerView recyclerView;
    private View emptyView;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter<Group> adapter;
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
        if (savedInstanceState != null) {
            showSearchResult = savedInstanceState.getBoolean(SAVE_KEY_SHOW_SEARCH_RESULT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_KEY_SHOW_SEARCH_RESULT, showSearchResult);
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
        adapter = MyAdapter.<Group>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_group_list_item)
                .data(presenter.loadGroups(showSearchResult))
                .onCreateView(holder -> {
                    // 头像
                    holder.circleImageViews(R.id.avatar_view);
                    // 群名、群号
                    holder.textViews(R.id.name_view, R.id.group_no_view);
                    // 二维码
                    holder.views(R.id.qr_code_view);
                })
                .onBindView((holder, item, position) -> {
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getGroupNo(), 50, 50);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    } else {
                        holder.circleImageView[0].setImageResource(R.drawable.ic_group_chat_white_24dp);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.textView[1].setText(item.getGroupNo());

                    holder.setClickedViews(holder.item, holder.view[0]);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (v.getId()) {
                        case R.id.qr_code_view:// qRCodeView
                            DialogUtils.showQRCode(
                                    activity(),
                                    QRCode.content()
                                            .setType(QRCode.Content.TYPE_GROUP)
                                            .setText(item.getGroupNo())
                                            .toString(),
                                    item.getGroupNo(),
                                    item.getName(),
                                    "扫一扫，加入群聊"
                            );
                            break;
                        default:// itemView
                            ModifyGroupActivity.start(activity(), item.getGroupNo());
                            break;
                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    // no-op
                    return false;
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
        if (App.activity() instanceof GroupListActivity) {
            App.showToast("群聊信息已更新", false);
        }
        setupEmptyView();
    }

}
