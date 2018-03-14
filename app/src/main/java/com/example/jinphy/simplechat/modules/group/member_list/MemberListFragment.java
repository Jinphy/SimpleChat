package com.example.jinphy.simplechat.modules.group.member_list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.modules.group.group_list.GroupListAdapter;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.StringUtils;

/**
 * DESC:
 * Created by jinphy, on 2018/3/14, at 15:31
 */
public class MemberListFragment extends BaseFragment<MemberListPresenter> implements MemberListContract.View {

    public static final String GROUP_NO = "GROUP_NO";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MemberListAdapter adapter;

    private String groupNo;

    public MemberListFragment() {
        // Required empty public constructor
    }

    /**
     * DESC:
     * Created by jinphy, on 2018/3/14, at 15:31
     */
    public static MemberListFragment newInstance(String groupNo) {
        MemberListFragment fragment = new MemberListFragment();
        fragment.groupNo = groupNo;
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
        return R.layout.fragment_member_list;
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
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MemberListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadMembers(groupNo));
    }

    @Override
    protected void registerEvent() {
        adapter.onClick((view, item, type, position) -> {
            Member member = (Member) item;
            App.showToast("click", false);
            switch (view.getId()) {
                case R.id.more_view:
                    break;
                default:
                    if (StringUtils.equal(member.getAccount(), member.getOwner())) {
                        ModifyUserActivity.start(activity());
                    } else {
                        ModifyFriendInfoActivity.start(activity(), member.getAccount());
                    }
                    break;
            }
        });
    }

}
