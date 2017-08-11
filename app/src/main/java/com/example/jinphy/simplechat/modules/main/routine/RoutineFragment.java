package com.example.jinphy.simplechat.modules.main.routine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends Fragment implements RoutineContract.View {

    private MainFragment fragment;

    private RoutineContract.Presenter presenter;

    FloatingActionButton fab;


    public RoutineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static RoutineFragment newInstance(MainFragment mainFragment) {
        RoutineFragment fragment = new RoutineFragment();
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
            this.presenter = fragment.getRoutinePresenter(this);
        }
        this.presenter.start();

    }

    @Override
    public void initFab() {
        fab = fragment.getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_smile_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setScaleX(1);
        fab.setScaleY(1);
        fab.setTranslationY(-IntConst.TOOLBAR_HEIGHT);
        fab.setOnClickListener(this::fabAction);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void setPresenter(RoutineContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void initView(View view) {

        GridLayout container = view.findViewById(R.id.grid_layout);

        List<CardView> items = getAllCardView(container);

        // TODO: 2017/8/11 设置图片，设置文字，设置点击监听,设置宽高


    }
    @Override
    public void fabAction(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void setMainFragment(@NonNull MainFragment mainFragment) {
        this.fragment = Preconditions.checkNotNull(mainFragment);
    }

    private List<CardView> getAllCardView(GridLayout parent) {
        if (parent.getChildCount() == 0) {
            return null;
        }
        List<CardView> result = new ArrayList<>(parent.getChildCount());
        for (int i = 0; i < parent.getChildCount(); i++) {
            result.add((CardView) parent.getChildAt(i));
        }
        return result;
    }
}
