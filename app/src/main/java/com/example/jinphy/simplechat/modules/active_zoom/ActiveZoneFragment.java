package com.example.jinphy.simplechat.modules.active_zoom;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveZoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveZoneFragment extends BaseFragment<ActiveZonePresenter>
        implements ActiveZoneContract.View {



    private RecyclerView recyclerView;
    private View headView;
    private ImageView backgroundView;
    private View foregroundView;
    private View toolbar;
    private View btnBack;



    public ActiveZoneFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_active_zoom;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        headView = view.findViewById(R.id.head_view);
        backgroundView = view.findViewById(R.id.background_view);
        foregroundView = view.findViewById(R.id.foreground_view);
        toolbar = view.findViewById(R.id.tool_bar);
        btnBack = view.findViewById(R.id.btn_back);
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {
        btnBack.setOnClickListener(view -> getActivity().finish());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ActiveZoneFragment.
     */
    public static ActiveZoneFragment newInstance() {
        ActiveZoneFragment fragment = new ActiveZoneFragment();
        return fragment;
    }

}
