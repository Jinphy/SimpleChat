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
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.menu.Routine;
import com.example.jinphy.simplechat.modules.active_zoom.ActiveZoneActivity;
import com.example.jinphy.simplechat.modules.group.group_list.GroupListActivity;
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

    private View root = null;
    private MyAdapter<Routine> adapter;


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
        //        GridLayout container = item.findViewById(R.id.grid_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void setupViews() {
        // TODO: 2017/8/11 设置图片，设置文字，设置点击监听,设置宽高
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = MyAdapter.<Routine>newInstance()
                .onInflate(viewType -> R.layout.main_tab_routine_item)
                .data(presenter.loadRoutines())
                .onCreateView(holder -> {
                    // icon
                    holder.imageViews(R.id.icon_view);
                    // tag
                    holder.textViews(R.id.tag_view);
                })
                .onBindView((holder, item, position) -> {
                    holder.imageView[0].setImageResource(item.getIconId());
                    holder.textView[0].setText(item.getTagId());
                    holder.setClickedViews(holder.item);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (item.getTagId()) {
                        case R.string.routine_active_zoom:
                            showActiveZoneActivity();
                            break;
                        case R.string.routine_group_chat:
                            showGroupListActivity();
                            break;
                        case R.string.routine_credit_card_address:
                            break;
                        case R.string.routine_certificates:
                            break;
                        case R.string.routine_scenic_spot:
                            break;
                        case R.string.routine_bus_route:
                            break;
                        case R.string.routine_food_menu:
                            break;
                        case R.string.routine_express:
                            break;
                        case R.string.routine_weather:
                            break;
                    }
                })
                .into(recyclerView);
    }


    @Override
    protected void registerEvent() {
    }


    @Override
    protected RoutinePresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getRoutinePresenter(this);
    }


    @Override
    public void showActiveZoneActivity() {
        ActiveZoneActivity.start(activity());
    }

    @Override
    public void showGroupListActivity() {
        GroupListActivity.start(activity(),false);
    }

}
