package com.example.jinphy.simplechat.modules.main.routine;

import android.app.Activity;
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
import com.example.jinphy.simplechat.modules.system_msg.SystemMsgActivity;
import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends BaseFragment<RoutinePresenter> implements RoutineContract.View {

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
    protected int getResourceId() {
        return R.layout.fragment_routine;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = MyAdapter.<Routine>newInstance()
                .onInflate(viewType -> R.layout.main_tab_routine_item)
                .data(Routine.create())
                .onCreateView(holder -> {
                    // icon
                    holder.imageViews(R.id.icon_view);
                    // tag
                    holder.textViews(R.id.tag_view);
                })
                .onBindView((holder, item, position) -> {
                    holder.imageView[0].setImageResource(item.icon);
                    holder.textView[0].setText(item.tag);
                    holder.setClickedViews(holder.item);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (item.icon) {
                        case R.drawable.ic_system_24dp:
                            SystemMsgActivity.start(activity());
                            break;
                        case R.drawable.ic_group_chat_24dp:
                            GroupListActivity.start(activity(), false);
                            break;
                        default:
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

}
