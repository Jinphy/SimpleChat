package com.example.jinphy.simplechat.modules.main.msg;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MsgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MsgFragment extends BaseFragment<MsgPresenter> implements MsgContract.View{

    private RecyclerView recyclerView;

    private FloatingActionButton fab;

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
    public void initFab() {
        this.fab = fragmentCallback.getFragment().getActivity().findViewById(R.id.fab);
        this.fab.setTranslationY(0);
        this.fab.setVisibility(View.GONE);
        this.fab.setImageResource(R.drawable.ic_arrow_up_24dp);
        this.fab.setOnClickListener(this::fabAction);
    }
    @Override
    public void fabAction(View view) {
        ((MainFragment) fragmentCallback.getFragment()).showBar(recyclerView);
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
                    ((MainFragment) fragmentCallback.getFragment()).hideBar(recyclerView);
                }
                if (total < -300) {
                    total=0;
                    ((MainFragment) fragmentCallback.getFragment()).showBar(recyclerView);
                }
            }
        };
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_msg;
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
        recyclerView.setAdapter(presenter.getAdapter());

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
    public void showChatWindow(MsgRecord item) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
    }
}
