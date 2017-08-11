package com.example.jinphy.simplechat.modules.main.friends;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.model.Friend;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.main.msg.MsgRecyclerViewAdapter;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment implements FriendsContract.View {

    private MainFragment fragment;

    private FriendsContract.Presenter presenter;

    RecyclerView recyclerView;

    FloatingActionButton fab;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance(MainFragment mainFragment) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.setMainFragment(mainFragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {

            this.presenter = fragment.getFriendsPresenter(this);
        }
        this.presenter.start();
    }

    @Override
    public void initFab() {
        this.fab = fragment.getActivity().findViewById(R.id.fab);
        this.fab.setTranslationY(0);
        this.fab.setVisibility(View.GONE);
        this.fab.setImageResource(R.drawable.ic_arrow_up_24dp);
        this.fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void fabAction(View view) {
        fragment.showBar();
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        initView(root);

        initData();

        return root;
    }

    @Override
    public void setPresenter(@NonNull FriendsContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void initView(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int total = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy>0时，向上滑动，反之向下
                total+=dy;
                if (total > 300) {
                    total=0;
                    fragment.hideBar();
                }
                if (total < -300) {
                    total=0;
                    fragment.showBar();
                }
            }
        });

    }

    @Override
    public void initData() {

        List<Friend> list = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            list.add(new Friend());
        }
        FriendsRecyclerViewAdapter adapter = new FriendsRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setMainFragment(@NonNull MainFragment fragment) {
        this.fragment = Preconditions.checkNotNull(fragment);
    }
}
