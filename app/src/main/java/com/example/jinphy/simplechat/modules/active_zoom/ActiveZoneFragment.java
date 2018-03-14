package com.example.jinphy.simplechat.modules.active_zoom;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveZoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveZoneFragment extends BaseFragment<ActiveZonePresenter>
        implements ActiveZoneContract.View {

    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;

    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private CardView headView;
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
        maxMarginVertical = ScreenUtils.dp2px(getContext(), 300);
        minMarginVertical = statusHeight + toolbarHeight;
        distanceVertical = maxMarginVertical - minMarginVertical;
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        onThirdScreenWidth = screenWidth/3;
        maxElevation = ScreenUtils.dp2px(getContext(), 20);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActiveZoneRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadBlogs());
    }

    @Override
    protected void registerEvent() {
        btnBack.setOnClickListener(view ->{
            if (statusAndToolbar.getAlpha() > 0.005) {
                onBackPressed();
            }
        });
        toolbar.setOnLongClickListener(view -> {
            recyclerView.smoothScrollToPosition(0);
            return true;
        });

    }


    private int moveOrientation;

    private int statusHeight;
    private int toolbarHeight;
    private int maxMarginVertical;
    private int minMarginVertical;
    private int distanceVertical;

    private float oldY;
    private float deltaY;
    @Override
    public boolean handleVerticalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                moveOrientation = VERTICAL;
                deltaY = event.getY()-oldY;
                oldY = event.getY();
                if (deltaY < 0 && canMoveUp()) {
                    // 向上滑动
                    moveVertical(getVerticalMoveFactor(deltaY));
                    return true;
                } else if (deltaY > 0 && canMoveDown()) {
                    // 向下滑动
                    moveVertical(getVerticalMoveFactor(deltaY));
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
    private float getVerticalMoveFactor(float deltaY) {
        float transY = headView.getTranslationY();
        transY += deltaY;
        transY = -transY;
        if (deltaY < 0 && transY > distanceVertical) {
            //向上滑动,并且appbarLayout滑到最高上限
            transY = distanceVertical;
        } else if (deltaY > 0 && transY < 0) {
            //向下滑动，并且appbarLayout滑到最低下限
            transY = 0;
        }

        float factor = transY / distanceVertical;

        return factor;
    }

    private boolean canMoveUp() {
        float transY = -headView.getTranslationY();
        boolean can = transY< distanceVertical;
//
//        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        int lastPosition = manager.findLastCompletelyVisibleItemPosition();
//
//        can = can && (lastPosition < presenter.getItemCount() - 1);
        return can;
    }

    private boolean canMoveDown() {
        float transY = headView.getTranslationY();
        boolean can = transY < 0;

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = manager.findFirstCompletelyVisibleItemPosition();

        can = can && (firstPosition == 0);
        return can;
    }

    @Override
    public void moveVertical(float factor) {
        float value = factor * distanceVertical;
        headView.setTranslationY(-value);
        setMargin(recyclerView, maxMarginVertical -value,0);
        ViewUtils.setAlpha(factor,foregroundView,statusAndToolbar);
        ViewUtils.setScaleXY(fab,1-factor*0.2f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float elevation = factor * ScreenUtils.dp2px(getContext(), 20);
            headView.setCardElevation(elevation);
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

    private float oldX;
    private float deltaX;
    private float downX;
    private float onThirdScreenWidth;
    private float screenWidth;
    private int maxElevation;
    private float startStatusAndToolBarAlpha;

    @Override
    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                oldX = downX;
                startStatusAndToolBarAlpha = statusAndToolbar.getAlpha();
                return false;
            case MotionEvent.ACTION_MOVE:
                moveOrientation = HORIZONTAL;
                deltaX = event.getX()-oldX;
                oldX = event.getX();
                if (canMoveHorizontal()) {
                    float factor = getHorizontalMoveFactor(deltaX);
                    moveHorizontal(factor);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (moveOrientation == HORIZONTAL) {
                    float factor  = rootView.getTranslationX()/screenWidth;
                    if (factor < 1.0f / 3) {
                        animateHorizontal(factor,0,false);
                    } else {
                        animateHorizontal(factor,1f,true);
                    }
                }

                return false;
            default:
                return false;
        }

    }

    private boolean canMoveHorizontal() {
        return (downX < onThirdScreenWidth) && (rootView.getTranslationX()>=0);
    }

    @Override
    public void moveHorizontal(float factor) {
        rootView.setTranslationX(factor *screenWidth);
        recyclerView.setAlpha(1-factor);
        statusAndToolbar.setAlpha((1-factor)* startStatusAndToolBarAlpha);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setElevation((float) (maxElevation *(1-factor*0.5)));
        }
    }

    private float getHorizontalMoveFactor(float deltaX) {
        float transX = rootView.getTranslationX();
        transX +=deltaX;

        if (deltaX < 0 && transX<0) {
            // 向左滑动
            transX = 0;
        }

        float factor = transX / screenWidth;

        return factor;
    }

    @Override
    public void animateHorizontal(float fromFactor, float toFactor,boolean exit) {
        float deltaFactor = Math.abs(toFactor-fromFactor);
        AnimUtils.just()
                .setFloat(fromFactor,toFactor)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration((long) (IntConst.DURATION_500 * deltaFactor))
                .onUpdateFloat(animator -> {
                    float factor = (float) animator.getAnimatedValue();
                    moveHorizontal(factor);
                })
                .onEnd(animator -> {
                    if (exit) {
                        getActivity().finish();
                    }
                })
                .animate();
    }

    @Override
    public boolean onBackPressed() {
        animateHorizontal(0,1,true);
        return false;
    }
}
