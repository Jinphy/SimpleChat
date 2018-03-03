package com.example.jinphy.simplechat.modules.main.routine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.modules.active_zoom.ActiveZoneActivity;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends BaseFragment<RoutinePresenter> implements RoutineContract.View {

    FloatingActionButton fab;

    private RecyclerView recyclerView;

    private int density;
    private RoutineRecyclerViewAdapter adapter;

    private View root = null;


    public RoutineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendsFragment.
     */
    public static RoutineFragment newInstance() {
        RoutineFragment fragment = new RoutineFragment();
        return fragment;
    }


    @Override
    public void initFab(Activity activity) {
        fab = activity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_smile_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setScaleX(1);
        fab.setScaleY(1);
        fab.setTranslationY(-ScreenUtils.getToolbarHeight(getContext()));
        fab.setOnClickListener(this::fabAction);
    }



    @Override
    protected int getResourceId() {
        return R.layout.fragment_routine;
    }

    @Override
    public void fabAction(View view) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = super.onCreateView(inflater, container, savedInstanceState);
        }
        return root;
    }


    @Override
    public void initData() {
        density = (int) ScreenUtils.getDensity(getContext());
    }

    @Override
    protected void findViewsById(View view) {
        //        GridLayout container = view.findViewById(R.id.grid_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void setupViews() {
        // TODO: 2017/8/11 设置图片，设置文字，设置点击监听,设置宽高
        adapter = presenter.getAdapter();
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void registerEvent() {

        adapter.onClick(presenter::handleRecyclerViewEvent);
    }


    @Override
    protected RoutinePresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getRoutinePresenter(this);
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


    @Override
    public void showActiveZoneActivity() {
        Intent intent = new Intent(getActivity(), ActiveZoneActivity.class);
        startActivity(intent);
    }
}
