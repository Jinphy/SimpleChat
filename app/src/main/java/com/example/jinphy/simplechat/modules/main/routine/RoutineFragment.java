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
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends BaseFragment implements RoutineContract.View {

    private MainFragment fragment;

    private RoutineContract.Presenter presenter;

    FloatingActionButton fab;

    private int density;


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
        fab.setTranslationY(-ScreenUtils.getToolbarHeight(getContext()));
        fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void setPresenter(RoutineContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_routine;
    }

    @Override
    public void fabAction(View view) {

    }

    @Override
    public void initData() {
        density = (int) ScreenUtils.getDensity(getContext());
    }

    @Override
    protected void findViewsById(View view) {
//        GridLayout container = view.findViewById(R.id.grid_layout);
    }

    @Override
    protected void setupViews() {
        // TODO: 2017/8/11 设置图片，设置文字，设置点击监听,设置宽高
    }

    @Override
    protected void registerEvent() {

    }

    @Override
    public void setMainFragment(@NonNull MainFragment mainFragment) {
        this.fragment = Preconditions.checkNotNull(mainFragment);
    }

//    private List<CardView> getAllCardView(GridLayout parent) {
//        if (parent.getChildCount() == 0) {
//            return null;
//        }
//        List<CardView> result = new ArrayList<>(parent.getChildCount());
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            result.add((CardView) parent.getChildAt(i));
//        }
//        return result;
//    }
}
