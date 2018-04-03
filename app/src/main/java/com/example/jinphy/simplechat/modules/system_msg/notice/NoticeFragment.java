package com.example.jinphy.simplechat.modules.system_msg.notice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * DESC:
 * Created by jinphy, on 2018/3/2, at 10:09
 */
public class NoticeFragment extends BaseFragment<NoticePresenter> implements NoticeContract.View {
    private RecyclerView recyclerView;
    private View emptyView;
    private MyAdapter<Message> adapter;

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
    public void onDestroy() {
        super.onDestroy();
        List<Message> msg = adapter.getData(message -> {
            if (message.isNew()) {
                message.setNew(false);
                return true;
            }
            return false;
        });

        if (msg.size() > 0) {
            presenter.updateMsg(msg);
        }
        EventBus.getDefault().post(new EBInteger(3));
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
        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    protected void setupViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = MyAdapter.<Message>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_system_notice_item)
                .data(presenter.loadNoticeMsg())
                .onCreateView(holder -> {
                    // title、time 、content
                    holder.textViews(
                            R.id.title_view,
                            R.id.time_view,
                            R.id.content_view
                    );
                    // newMsg
                    holder.views(R.id.new_msg_view);
                })
                .onBindView((holder, item, position) -> {

                    holder.textView[0].setText(TextUtils.isEmpty(item.getExtra()) ? "无标题" : item
                            .getExtra());
                    holder.textView[1].setText(StringUtils.formatDate(Long.valueOf(item
                            .getCreateTime())));
                    holder.textView[2].setText(item.getContent());

                    holder.view[0].setVisibility(item.isNew() ? View.VISIBLE : View.GONE);

                    holder.setClickedViews(holder.item);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (v.getId()) {
                        case R.id.item_view:
                            // TODO: 2018/4/2
                            break;
                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    MyMenu.create(activity())
                            .width(200)
                            .item("删除", (menu, item1) -> {
                                presenter.deleteMsg(item);
                                EventBus.getDefault().post(new EBInteger(true, 3));
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
        NoticeActivity activity = (NoticeActivity) activity();
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


}
