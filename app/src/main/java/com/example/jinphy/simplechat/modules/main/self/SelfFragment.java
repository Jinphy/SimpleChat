package com.example.jinphy.simplechat.modules.main.self;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.w3c.dom.Text;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelfFragment extends BaseFragment<SelfPresenter> implements SelfContract.View {

    private MainFragment fragment;

    private FloatingActionButton fab;

    private LinearLayout contentLayout;
    private CardView headView;
    private ImageView avatarView;
    private TextView nameText;
    private TextView dateText;

    private float density;

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
    public void onResume() {
        super.onResume();
        if (this.presenter == null) {
            this.presenter = getPresenter();
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
        fab.setTranslationY(-ScreenUtils.getToolbarHeight(getContext()));
        fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void fabAction(View view) {

    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_self;
    }

    @Override
    public void initData() {
        density = ScreenUtils.getDensity(getContext());

        distance = ScreenUtils.dp2px(IntConst.HEAD_VIEW_HEIGHT - IntConst.TOOLBAR_HEIGHT,density);
        baseTransY = -distance;
        avatarViewTransXDistance = ScreenUtils.dp2px(IntConst.NEGATIVE_150, density);
        avatarViewTransYDistance = ScreenUtils.dp2px(IntConst.POSITIVE_120, density);
        nameTextTransXDistance = ScreenUtils.dp2px(IntConst.POSITIVE_50, density);
//        distance = (IntConst.HEAD_VIEW_HEIGHT - IntConst.TOOLBAR_HEIGHT)*density;
//        baseTransY = -distance;
//        avatarViewTransXDistance = IntConst.NEGATIVE_150 * density;
//        avatarViewTransYDistance = IntConst.POSITIVE_120 * density;
//        nameTextTransXDistance = IntConst.POSITIVE_50 * density;
    }

    @Override
    protected void findViewsById(View view) {
        contentLayout = view.findViewById(R.id.content_layout);
        headView = view.findViewById(R.id.head_view);
        avatarView = headView.findViewById(R.id.avatar);
        nameText = headView.findViewById(R.id.name);
        dateText = headView.findViewById(R.id.date);
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {

    }

    @Override
    public void setMainFragment(@NonNull MainFragment mainFragment) {
        this.fragment = Preconditions.checkNotNull(mainFragment);
    }


    int distance;
    float avatarViewScaleDistance = 0.68f;
    float avatarViewTransXDistance;
    float avatarViewTransYDistance;
    float nameTextTransXDistance;
    int baseTransY;
    float oldY;

    boolean isHeadViewMoveUp = false;
    boolean hasActionMove = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                hasActionMove = true;
                float deltaY = event.getY() - oldY;
                oldY = event.getY();
                float faction = getMoveFaction(deltaY);
                transform(faction);
                return true;
            case MotionEvent.ACTION_UP:
                if (hasActionMove) {
                    hasActionMove = false;
                    float movedDistance = -headView.getTranslationY();
                    if (movedDistance > distance / 2) {
                        // 滑动超过一半
                        animate(1);
                        isHeadViewMoveUp = true;
                    } else {
                        // 滑动未超过一半
                        animate(0);
                        isHeadViewMoveUp = false;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private boolean checkViewNull(String str) throws Exception {
        int count=0;
        do {
            if (headView != null) {
                break;
            }
            Thread.sleep(30);
            count++;
        }while(count<10);

        if (headView != null) {
            return false;
        }
        return true;
    }

    // 手指移动过程中的动画转换
    private void transform(float faction) {
        Observable.just("check null")
                .map(this::checkViewNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isNull->transform(isNull,faction));
    }


    private void transform(boolean isNull, float faction) {
        if (!isNull) {
            headView.setTranslationY(-faction*distance);
            // TODO: 2017/8/13 headView和statusBar背景颜色初始值设置为相应头像的Palette颜色，
            // TODO: 2017/8/13 headView的结束颜色设置为R.color.colorPrimary
            // TODO: 2017/8/13 statusBar的结束颜色设置为 R.color.colorPrimaryDark
            headView.setBackgroundColor(ColorUtils.rgbColorByFactor(
                    ContextCompat.getColor(getContext(),R.color.colorPrimaryDark),
                    ContextCompat.getColor(getContext(),R.color.colorPrimary),
                    faction
            ));
            contentLayout.setTranslationY(distance*(1-faction));
            ViewUtils.setScaleXY(avatarView,1-faction*avatarViewScaleDistance);
            avatarView.setTranslationX(faction*avatarViewTransXDistance);
            avatarView.setTranslationY(faction*avatarViewTransYDistance);
            nameText.setTranslationX(faction*nameTextTransXDistance);
            dateText.setAlpha(1-faction);
        }
    }

    // 手指松开后的动画
    private void animate(float faction) {
        float startFaction = -headView.getTranslationY()/distance;
        AnimUtils.just()
                .setDuration(IntConst.DURATION_250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setFloat(startFaction,faction)
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    transform(value);
                })
                .animate();
    }


    //获取当前headView应该滑动的百分比
    private float getMoveFaction(float deltaY) {
        float transY = headView.getTranslationY();
        transY += deltaY;
        if (deltaY < 0 && transY < baseTransY) {
            //向上滑动,并且appbarLayout滑到最高上限
            transY = baseTransY;
        } else if (deltaY > 0 && transY > 0) {
            //向下滑动，并且appbarLayout滑到最低下限
            transY = 0;
        } else {
        }

        float faction = -transY / distance;

        return faction;
    }

    @Override
    public void handleOnViewPagerScrolled(int position, float offset, int offsetPixels) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                if (isHeadViewMoveUp) {
                    transform(1);
                    ViewUtils.setAlpha(offset,avatarView,nameText);
                } else {
                    transform(1-offset);
                }
                break;
            case 3:
                break;
            default:
                break;

        }
    }
}
