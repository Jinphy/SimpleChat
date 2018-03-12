package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.modules.system_msg.new_friend.NewFriendRecyclerViewAdapter;

/**
 * DESC:
 * Created by jinphy, on 2018/3/12, at 21:59
 */
public class NewMemberFragment extends BaseFragment<NewMemberPresenter> implements NewMemberContract.View {


    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

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
    protected int getResourceId() {
        return R.layout.fragment_new_member;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {

    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {

    }


}
