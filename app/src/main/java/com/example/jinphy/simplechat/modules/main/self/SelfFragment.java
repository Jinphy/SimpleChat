package com.example.jinphy.simplechat.modules.main.self;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelfFragment extends Fragment implements SelfContract.View {

    private MainFragment fragment;

    private SelfContract.Presenter presenter;

    private FloatingActionButton fab;

    public SelfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static SelfFragment newInstance(MainFragment mainFragment) {
        SelfFragment fragment = new SelfFragment();
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
        if (this.presenter == null) {
            this.presenter = fragment.getSelfPresenter(this);
        }
        this.presenter.start();
    }

    @Override
    public void initFab() {
        fab = fragment.getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setScaleX(1);
        fab.setScaleY(1);
        fab.setTranslationY(-IntConst.TOOLBAR_HEIGHT);
        fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void fabAction(View view) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_self, container, false);

        initView(root);

        initData();

        return root;
    }

    @Override
    public void setPresenter(SelfContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void setMainFragment(@NonNull MainFragment mainFragment) {
        this.fragment = Preconditions.checkNotNull(mainFragment);
    }

    @Override
    public boolean onTouch(MotionEvent event) {

        return false;
    }
}
