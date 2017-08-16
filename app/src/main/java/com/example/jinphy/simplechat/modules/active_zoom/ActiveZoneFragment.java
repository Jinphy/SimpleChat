package com.example.jinphy.simplechat.modules.active_zoom;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveZoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveZoneFragment extends BaseFragment<ActiveZonePresenter>
        implements ActiveZoneContract.View {

    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private View headView;
    private ImageView backgroundView;
    private View foregroundView;
    private View statusAndToolbar;
    private View toolbar;
    private View btnBack;
    private ActiveZoneRecyclerViewAdapter adapter;


    public ActiveZoneFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActiveZoneFragment.
     */
    public static ActiveZoneFragment newInstance() {
        ActiveZoneFragment fragment = new ActiveZoneFragment();
        return fragment;
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_active_zoom;
    }

    @Override
    protected void initData() {
        statusHeight = ScreenUtils.getStatusBarHeight(getContext());
        toolbarHeight = ScreenUtils.getToolbarHeight(getContext());
        maxMargin = ScreenUtils.dp2px(getContext(), 300);
        endToolbarColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        startToolbarColor = ColorUtils.setAlpha(endToolbarColor, 0);
        endStatusColor = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        startStaturColor = ColorUtils.setAlpha(endStatusColor, 0);
        minMargin = statusHeight + toolbarHeight;
        distance = maxMargin-minMargin;
    }

    @Override
    protected void findViewsById(View view) {

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recycler_view);
        headView = view.findViewById(R.id.head_view);
        backgroundView = view.findViewById(R.id.background_view);
        foregroundView = view.findViewById(R.id.foreground_view);
        statusAndToolbar = view.findViewById(R.id.status_and_tool_bar);
        toolbar = view.findViewById(R.id.tool_bar);
        btnBack = view.findViewById(R.id.btn_back);
    }

    @Override
    protected void setupViews() {
        adapter = presenter.getAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void registerEvent() {
        btnBack.setOnClickListener(view -> getActivity().finish());
        toolbar.setOnLongClickListener(view -> {
            recyclerView.smoothScrollToPosition(0);
            return true;
        });

    }


    private int statusHeight;
    private int toolbarHeight;
    private int maxMargin;
    private int minMargin;
    private int startToolbarColor;
    private int endToolbarColor;
    private int startStaturColor;
    private int endStatusColor;

    private int distance;

    private float oldY;
    private float deltaY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                deltaY = event.getY()-oldY;
                oldY = event.getY();
                if (deltaY < 0 && canMoveUp()) {
                    // 向上滑动
                    move(getMoveFactor(deltaY));
                    return true;
                } else if (deltaY > 0 && canMoveDown()) {
                    // 向下滑动
                    move(getMoveFactor(deltaY));
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                return false;
            default:
                return false;

        }
    }

    //获取当前headView应该滑动的百分比
    private float getMoveFactor(float deltaY) {
        float transY = headView.getTranslationY();
        transY += deltaY;
        transY = -transY;
        if (deltaY < 0 && transY > distance) {
            //向上滑动,并且appbarLayout滑到最高上限
            transY = distance;
        } else if (deltaY > 0 && transY < 0) {
            //向下滑动，并且appbarLayout滑到最低下限
            transY = 0;
        }

        float factor = transY / distance;

        return factor;
    }

    private boolean canMoveUp() {
        float transY = -headView.getTranslationY();
        boolean can = transY<distance;

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastPosition = manager.findLastCompletelyVisibleItemPosition();

        can = can && (lastPosition < presenter.getItemCount() - 1);
        return can;
    }

    private boolean canMoveDown() {
        float transY = headView.getTranslationY();
        boolean can = transY < 0;

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = manager.findFirstCompletelyVisibleItemPosition();

        Log.e(ActiveZoneFragment.this.getClass().getSimpleName(), "firstPosition=" + firstPosition);

        can = can && (firstPosition == 0);
        return can;
    }

    private void move(float factor) {
        float value = factor * distance;
        headView.setTranslationY(-value);
        setMargin(recyclerView,maxMargin-value,0);
        ViewUtils.setAlpha(factor,foregroundView,statusAndToolbar);
        ViewUtils.setScaleXY(fab,1-factor*0.2f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float elevation = factor * ScreenUtils.dp2px(getContext(), 10);
            headView.setElevation(elevation);
            statusAndToolbar.setElevation(elevation);
        }
    }

    //设置view的margin
    private void setMargin(View view, float marginTop,float marginBottom) {
        if (view == null) {
            return;
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(lp.leftMargin, (int) marginTop,lp.rightMargin, (int) marginBottom);
        view.setLayoutParams(lp);
        view.requestLayout();
    }
}
