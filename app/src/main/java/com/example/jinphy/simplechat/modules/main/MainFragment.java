package com.example.jinphy.simplechat.modules.main;

import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.main.friends.FriendsContract;
import com.example.jinphy.simplechat.modules.main.friends.FriendsFragment;
import com.example.jinphy.simplechat.modules.main.friends.FriendsPresenter;
import com.example.jinphy.simplechat.modules.main.msg.MsgContract;
import com.example.jinphy.simplechat.modules.main.msg.MsgFragment;
import com.example.jinphy.simplechat.modules.main.msg.MsgPresenter;
import com.example.jinphy.simplechat.modules.main.routine.RoutineContract;
import com.example.jinphy.simplechat.modules.main.routine.RoutineFragment;
import com.example.jinphy.simplechat.modules.main.routine.RoutinePresenter;
import com.example.jinphy.simplechat.modules.main.self.SelfContract;
import com.example.jinphy.simplechat.modules.main.self.SelfFragment;
import com.example.jinphy.simplechat.modules.main.self.SelfPresenter;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements MainContract.View {

    public static final int MSG_FRAGMENT = 0;
    public static final int FRIEND_FRAGMENT = 1;
    public static final int ROUTINE_FRAGMENT = 2;
    public static final int SELF_FRAGMENT = 3;

    private ViewPager viewPager;

    MsgFragment msgFragment ;
    FriendsFragment friendsFragment ;
    RoutineFragment routineFragment;
    SelfFragment selfFragment;

    private View appbarLayout;
    private Toolbar toolbar;

    private View bottomBar;
    private FloatingActionButton fab;

    private LinearLayout btnMsg;
    private LinearLayout btnFriends;
    private LinearLayout btnRoutine;
    private LinearLayout btnSelf;


    private ViewGroup[] btn;
    private int[] iconsOpen;
    private int[] iconsClose;

    private int selectedTab = 0;

    private int density;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = getPresenter();
        }
        presenter.start();
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_main;
    }


    @Override
    public void initData() {
        // 底部导航栏的按钮
        btn = new LinearLayout[]{
                btnMsg,
                btnFriends,
                btnRoutine,
                btnSelf
        };
        // 底部导航栏按钮的打开图标
        iconsOpen = new int[]{
                R.drawable.ic_msg_open_24dp,
                R.drawable.ic_friends_open_24dp,
                R.drawable.ic_routine_open_24dp,
                R.drawable.ic_self_open_24dp
        };

        // 底部导航栏按钮的关闭图标
        iconsClose = new int[]{
                R.drawable.ic_msg_close_24dp,
                R.drawable.ic_friends_close_24dp,
                R.drawable.ic_routine_close_24dp,
                R.drawable.ic_self_close_24dp
        };


        density = (int) ScreenUtils.getDensity(getContext());
    }

    private MainViewPagerAdapter getAdapter() {
        List<Fragment> fragments = generateFragments();
        return new MainViewPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
    }

    @Override
    protected void findViewsById(View view) {
        MainActivity activity = (MainActivity) getActivity();

        appbarLayout = activity.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        viewPager = view.findViewById(R.id.view_pager);
        bottomBar = view.findViewById(R.id.bottom_bar);
        btnMsg = view.findViewById(R.id.btn_msg);
        btnFriends = view.findViewById(R.id.btn_friends);
        btnRoutine = view.findViewById(R.id.btn_routine);
        btnSelf = view.findViewById(R.id.btn_self);

    }

    @Override
    protected void setupViews() {
        viewPager.setAdapter(getAdapter());

        // 为viewpager中的每个item设置相应的fab属性
        initFab(selectedTab);
    }

    @Override
    protected void registerEvent() {
        btnMsg.setOnClickListener(this::selectFragment);
        btnFriends.setOnClickListener(this::selectFragment);
        btnRoutine.setOnClickListener(this::selectFragment);
        btnSelf.setOnClickListener(this::selectFragment);

        viewPager.addOnPageChangeListener(getPageChangeListener());
    }

    private ViewPager.OnPageChangeListener getPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                // position表示当前第一个可见的pager的位置，
                // offset表示position位置的pager的偏移百分比，正数
                // offsetPixels表示position位置的pager的偏移像素，正数
                // 当position 位置的pager位于正中间是，上面两个偏移量为0


                // 处理fab的动画
                handleFabOnViewPagerScrolled(position,offset,offsetPixels);

                //处理appbar的动画
                handleBarOnViewPagerScrolled(position, offset, offsetPixels);

                // 第四页的动画处理
                selfFragment.handleOnViewPagerScrolled(position, offset, offsetPixels);

            }

            @Override
            public void onPageSelected(int position) {

                // 处理toolbar和bottomBar
                handleBarOnViewPagerSelected(position);

                // 处理Tab
                handleTabOnViewPagerSelected(position);

                // 处理fab
                handleFabOnViewPagerSelected(position);

                selectedTab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private void handleFabOnViewPagerScrolled(int position, float offset, int offsetPixels) {
        switch (position) {
            case 0:
                if (fab.getVisibility() == View.VISIBLE) {
                    int result = selectedTab==0?animateFab(1-offset):animateFab(offset);
                }
                break;
            case 1:// 第二页中fab的动画
                if (offsetPixels <= 0) {
                    animateFab(0, 0, R.drawable.ic_arrow_up_24dp, View.GONE);
                } else if (offsetPixels < 100) {
                    animateFab(offset, -ScreenUtils.getToolbarHeight(getContext()),
                            R.drawable.ic_smile_24dp, View.VISIBLE);
                } else {
                    animateFab(offset);
                }
                break;
            case 2: //当前可见的第一个pager为第三个fragment
                if (offset < 0.5f) {
                    animateFab(1 - offset * 2,  -ScreenUtils.getToolbarHeight(getContext()),
                            R.drawable.ic_smile_24dp, View.VISIBLE);
                } else {
                    animateFab(offset * 2 - 1,  -ScreenUtils.getToolbarHeight(getContext()),
                            R.drawable.ic_edit_24dp, View.VISIBLE);
                }
                break;
            case 3:
                break;
            default:
                break;
        }

    }

    private void handleBarOnViewPagerScrolled(int position, float offset, int offsetPixels) {
        switch (position) {
            case 0:
                appbarLayout.setTranslationX(0);
                break;
            case 1:
                appbarLayout.setTranslationX(0);
                break;
            case 2:
                appbarLayout.setTranslationX(-offsetPixels);
                break;
            case 3:
                appbarLayout.setTranslationX(-ScreenUtils.getScreenWidth(getContext()));
                break;
        }
    }

    private void handleFabOnViewPagerSelected(int position) {
        initFab(position);
    }

    private void handleTabOnViewPagerSelected(int position) {
        selectTab(position, false);
    }

    private void handleBarOnViewPagerSelected(int position) {
        if (selectedTab != position) {
            if (position == 0) {
                showBar(msgFragment.getRecyclerView());
            } else if (position == 1) {
                showBar(friendsFragment.getRecyclerView());
            } else {
                showBar(null);
            }
        }
    }


    /**
     *
     * @param scale
     * @param options 可选项，是个可变长度数组，接受前三个元素
     *                第一个表示transY
     *                第二个表示图片id
     *                第三个表示Visibility
     * */
    private int animateFab(float scale, int... options) {
        switch (options.length) {
            case 0:
                ViewUtils.setScaleXY(fab,scale);
                break;
            case 1:
                ViewUtils.setScaleXY(fab,scale);
                fab.setTranslationY(options[0]);
                break;
            case 2:
                ViewUtils.setScaleXY(fab,scale);
                fab.setTranslationY(options[0]);
                fab.setImageResource(options[1]);
                break;
            default:
                ViewUtils.setScaleXY(fab,scale);
                fab.setTranslationY(options[0]);
                fab.setImageResource(options[1]);
                fab.setVisibility(options[2]);
                break;
        }
        return 0;
    }


    @Override
    public void initFab(int position) {
        switch (position) {
            case MSG_FRAGMENT:
                msgFragment.initFab();
                break;
            case FRIEND_FRAGMENT:
                friendsFragment.initFab();
                break;
            case ROUTINE_FRAGMENT:
                routineFragment.initFab();
                break;
            case SELF_FRAGMENT:
                selfFragment.initFab();
                break;
            default:
                break;
        }

    }

    @Override
    public void setToolbarAlpha(float faction) {
        toolbar.setAlpha(1 - faction);
    }

    @Override
    public void setHeadViewTransY(float faction, int baseTransY, int distance) {
//        appbarLayout.setTranslationY(baseTransY + faction * distance);
//        avatarView.setAlpha(faction);
//        nameText.setAlpha(faction);
    }



    @Override
    public void selectFragment(View view) {
        switch (view.getId()) {
            case R.id.btn_msg:
                selectTab(0, true);
                break;
            case R.id.btn_friends:
                selectTab(1, true);
                break;
            case R.id.btn_routine:
                selectTab(2, true);
                break;
            case R.id.btn_self:
                selectTab(3, true);
                break;
            default:
                break;
        }
    }


    @Override
    public List<Fragment> generateFragments() {
        msgFragment = MsgFragment.newInstance(this);
        friendsFragment = FriendsFragment.newInstance(this);
        routineFragment = RoutineFragment.newInstance(this);
        selfFragment = SelfFragment.newInstance(this);

        msgFragment.setCallback(this::getMsgPresenter);
        friendsFragment.setCallback(this::getFriendsPresenter);
        routineFragment.setCallback(this::getRoutinePresenter);
        selfFragment.setCallback(this::getSelfPresenter);

        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(msgFragment);
        fragments.add(friendsFragment);
        fragments.add(routineFragment);
        fragments.add(selfFragment);

        getMsgPresenter(msgFragment);
        getFriendsPresenter(friendsFragment);
        getRoutinePresenter(routineFragment);
        getSelfPresenter(selfFragment);
        return fragments;
    }

    @Override
    public MsgPresenter getMsgPresenter(Fragment fragment) {

        return new MsgPresenter((MsgContract.View) fragment);
    }

    @Override
    public FriendsPresenter getFriendsPresenter(Fragment fragment) {

        return new FriendsPresenter((FriendsContract.View) fragment);
    }

    @Override
    public RoutinePresenter getRoutinePresenter(Fragment fragment) {

        return new RoutinePresenter((RoutineContract.View) fragment);
    }

    @Override
    public SelfPresenter getSelfPresenter(Fragment fragment) {

        return new SelfPresenter((SelfContract.View) fragment);
    }


    /**
     * 选择指定的tab标签项
     *
     * */
    @Override
    public void selectTab(int position, boolean setItem) {
        showNormalState(selectedTab);
        showSelectedState(position);
        if (setItem) {
            viewPager.setCurrentItem(position);
        }
    }


    /**
     * 设置底部tab标签按钮额状态为未选择状态
     * */
    @Override
    public void showNormalState(int position) {
        ((ImageView) btn[position]
                .getChildAt(0))
                .setImageResource(iconsClose[position]);
        ((TextView) btn[position]
                .getChildAt(1)).
                setTextColor(ContextCompat.getColor(getContext(), R.color.half_alpha_gray));
    }

    /**
     * 设置底部tab标签按钮的状态为选择状态
     *
     *
     * */
    @Override
    public void showSelectedState(int position) {

        ((ImageView) btn[position]
                .getChildAt(0))
                .setImageResource(iconsOpen[position]);
        ((TextView) btn[position]
                .getChildAt(1)).
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

    }

    private boolean isBarVisible = true;

    private AnimatorSet animatorSet = null;

    /**
     * 移动toolbar和bottomBar和scale fab
     *
     * @see MainFragment#showBar(View)
     * @see MainFragment#hideBar(View)
     * */
    @Override
    public void animateBar(View view, float fromValue, float toValue,boolean showBar) {

        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.end();
        }

        int appbarHeight = appbarLayout.getHeight();
        int bottomBarHeight = bottomBar.getHeight();
        animatorSet = AnimUtils.just()
                .setFloat(fromValue,toValue)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_500)
                .onStart(animator ->{
                    if (showBar) {
                        appbarLayout.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                } )
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    float margin = appbarHeight * (1-value);
                    appbarLayout.setTranslationY(value * (-appbarHeight));
                    bottomBar.setTranslationY(value * bottomBarHeight);
                    ViewUtils.setScaleXY(fab,value);
                    setStatusBarColor(value);
                    setMargin(view,margin);
                })
                .onEnd(animator -> {
                    if (showBar) {
                        fab.setVisibility(View.GONE);
                    } else {
                        appbarLayout.setVisibility(View.GONE);
                        bottomBar.setVisibility(View.GONE);
                    }
                })
                .build();
        animatorSet.start();

    }

    /**
     * 隐藏toolbar和bottomBar，同时显示fab
     *
     * @param view 是一个RecyclerView，传该参数的目的是为了在
     * 移动toolbar和bottomBar时，更新RecyclerView的margin值
     * */
    @Override
    public void hideBar(View view) {
        if (isBarVisible) {
            isBarVisible = false;
            animateBar(view,0,1,false);

        }

    }




    /**
     * 显示toolbar和bottomBar，同时隐藏fab
     *
     * @param view 是一个RecyclerView，传该参数的目的是为了在
     * 移动toolbar和bottomBar时，更新RecyclerView的margin值
     * */
    @Override
    public void showBar(View view) {
        if (!isBarVisible) {
            isBarVisible = true;
            animateBar(view,1,0,true);

        }
    }

    @Override
    public void setStatusBarColor(float factor) {
        int primaryDark = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        int accent = ContextCompat.getColor(getContext(), R.color.colorAccent);
        int color = ColorUtils.rgbColorByFactor(primaryDark, accent, factor);
        ScreenUtils.setStatusBarColor(getActivity(),color);
    }

    //设置View的margin，用在移动toolbar和bottomBar时改变其他View
    //的margin
    private void setMargin(View view, float margin) {
        if (view == null) {
            return;
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(lp.leftMargin, (int) margin,lp.rightMargin, (int) margin);
        view.setLayoutParams(lp);
        view.requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (selectedTab == 3) {
            return selfFragment.dispatchTouchEvent(event);
        }
        return false;

    }


    @Override
    public int currentItemPosition() {
        return selectedTab;
    }
}
