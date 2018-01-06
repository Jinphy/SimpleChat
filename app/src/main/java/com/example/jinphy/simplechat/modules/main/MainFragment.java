package com.example.jinphy.simplechat.modules.main;

import android.animation.AnimatorSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.models.event_bus.EBLoginInfo;
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
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements MainContract.View {

    private static final String TAG = "MainFragment";
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
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     *
     * @return A new app of fragment MainFragment.
     */
    public static MainFragment newInstance() throws Exception{
        Thread.sleep(1000);

        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void initData() {
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


    @Override
    protected int getResourceId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void findViewsById(View view) {
        MainActivity activity = (MainActivity) getActivity();

        appbarLayout = activity.findViewById(R.id.appbar_layout);
        toolbar =  activity.findViewById(R.id.toolbar);

        fab =  activity.findViewById(R.id.fab);
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

        // 底部导航栏的按钮
        btn = new LinearLayout[]{
                btnMsg,
                btnFriends,
                btnRoutine,
                btnSelf
        };

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

    //---------------私有函数---------------------------------------------

    private MainViewPagerAdapter getAdapter() {
        List<Fragment> fragments = generateFragments();
        FragmentManager manager = this.getChildFragmentManager();
        return new MainViewPagerAdapter(manager, fragments);
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

    // 获取一个fragment时，判断fragment是否存在于FragmentManager中，
    // 如果存在，在从FragmentManager中获取，否则生成一个新的fragment对象实例
    private <T extends Fragment> T getFragment(int position) {
        String tag = MainViewPagerAdapter.getItemTag(viewPager, position);
        Fragment fragment = this.getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            return (T) fragment;
        }

        switch (position) {
            case MSG_FRAGMENT:
                return (T) MsgFragment.newInstance();
            case FRIEND_FRAGMENT:
                return (T) FriendsFragment.newInstance();
            case ROUTINE_FRAGMENT:
                return (T) RoutineFragment.newInstance();
            case SELF_FRAGMENT:
                return (T) SelfFragment.newInstance();
            default:
                return null;
        }
    }

    //----------EventBus--------------------------------------------------

    // 接受来自登录界面或者欢迎页传递过来的account，然后根据account获取用户信息
    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void accountEvent(EBLoginInfo info) {
        BaseApplication.e(TAG, "accountEvent: account = " + info.account);
    }













    //----------mvp中的View函数--------------------------------------------

    @Override
    public void initFab(int position) {

        switch (position) {
            case MSG_FRAGMENT:
                msgFragment.initFab(getActivity());
                break;
            case FRIEND_FRAGMENT:
                friendsFragment.initFab(getActivity());
                break;
            case ROUTINE_FRAGMENT:
                routineFragment.initFab(getActivity());
                break;
            case SELF_FRAGMENT:
                selfFragment.initFab(getActivity());
                break;
            default:
                break;
        }

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

        msgFragment = getFragment(MSG_FRAGMENT);
        friendsFragment = getFragment(FRIEND_FRAGMENT);
        routineFragment = getFragment(ROUTINE_FRAGMENT);
        selfFragment = getFragment(SELF_FRAGMENT);

        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(msgFragment);
        fragments.add(friendsFragment);
        fragments.add(routineFragment);
        fragments.add(selfFragment);

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
    public boolean handleVerticalTouchEvent(MotionEvent event) {
        if (selectedTab == 3) {
            return selfFragment.handleVerticalTouchEvent(event);
        }
        return false;
    }

    @Override
    public int currentItemPosition() {
        return selectedTab;
    }
}
