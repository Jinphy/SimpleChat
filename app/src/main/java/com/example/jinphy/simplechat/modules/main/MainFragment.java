package com.example.jinphy.simplechat.modules.main;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements MainContract.View {

    private MainActivity activity;

    private MainContract.Presenter presenter;

    private ViewPager viewPager;
    private MainViewPagerAdapter adapter;

    private View appbarLayout;
    private Toolbar toolbar;
    private CardView headView;
    private ImageView avatarView;
    private TextView nameText;

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


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = activity.getPresenter(this);
        }
        presenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.activity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        initView(root);

        initData();

        return root;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }


    @Override
    public void initView(View view) {

        appbarLayout = activity.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        headView = (CardView) activity.findViewById(R.id.head_view);
        avatarView = headView.findViewById(R.id.avatar);
        nameText = headView.findViewById(R.id.name);

        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        viewPager = view.findViewById(R.id.view_pager);
        bottomBar = view.findViewById(R.id.bottom_bar);
        btnMsg = view.findViewById(R.id.btn_msg);
        btnFriends = view.findViewById(R.id.btn_friends);
        btnRoutine = view.findViewById(R.id.btn_routine);
        btnSelf = view.findViewById(R.id.btn_self);


        btnMsg.setOnClickListener(this::selectFragment);
        btnFriends.setOnClickListener(this::selectFragment);
        btnRoutine.setOnClickListener(this::selectFragment);
        btnSelf.setOnClickListener(this::selectFragment);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean isShowingHeadView = false;
            int selectedTab = 0;

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                // position表示当前第一个可见的pager的位置，
                // offset表示position位置的pager的偏移百分比，正数
                // offsetPixels表示position位置的pager的偏移像素，正数
                // 当position 位置的pager位于正中间是，上面两个偏移量为0
                activity.e("position = " + position);
                activity.e("offset = " + offset);
                activity.e("offsetPixels = " + offsetPixels);
                activity.e("=============================================");


                switch (position) {
                    case 0:
                        //                        if (isShowingHeadView) {
                        //                            headView.setVisibility(View.GONE);
                        //                            isShowingHeadView=false;
                        //                        }
                        break;
                    case 1:
                        if (offsetPixels <= 0) {
                            fab.setTranslationY(0);
                            scaleFab(0);
                            fab.setVisibility(View.GONE);
                        } else if (offsetPixels < 100) {
                            fab.setVisibility(View.VISIBLE);
                            fab.setTranslationY(-IntConst.TOOLBAR_HEIGHT);
                            fab.setImageResource(R.drawable.ic_smile_24dp);
                            scaleFab(offset);
                        } else {
                            scaleFab(offset);
                        }

                        break;
                    case 2: //当前可见的第一个pager为第三个fragment
                        if (offsetPixels == 0) {
                            if (isShowingHeadView) {
                                setHeadViewTransY(1, baseTransY, distance);
                                toolbar.setVisibility(View.VISIBLE);
                                headView.setVisibility(View.GONE);
                                isShowingHeadView = false;
                            }
                        } else if (ScreenUtils.getScreenWidth(getContext()) - offsetPixels < 5) {
                            toolbar.setVisibility(View.GONE);
                            setHeadViewTransY(1f, baseTransY, distance);
                            isShowingHeadView = true;
                        } else {
                            headView.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.VISIBLE);
                            isShowingHeadView = true;
                            setHeadViewTransY(offset, baseTransY, distance);
                        }
                        if (offset < 0.5f) {
                            fab.setImageResource(R.drawable.ic_smile_24dp);
                            scaleFab(1 - offset * 2);
                        } else {
                            fab.setImageResource(R.drawable.ic_edit_24dp);
                            scaleFab(offset * 2 - 1);
                        }
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageSelected(int position) {

                if (selectedTab == 3 && selectedTab - position > 1) {
                    // 从第四页跳到第一或第二页,此时没有经过第三页，所以上面的回调函数将不处理动画
                    // 所以这里要另外设置
                    isShowingHeadView = false;
                    if (isHeadViewScrolledUp) {

                    } else {
                        AnimUtils.just(appbarLayout)
                                .setTranY(baseTransY)
                                .setDuration(IntConst.DURATION_500)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .onEnd(animator -> {
                                    headView.setVisibility(View.GONE);
                                    appbarLayout.setTranslationY(0);
                                    toolbar.setVisibility(View.VISIBLE);
                                    AnimUtils.just(toolbar)
                                            .setAlpha(0, 1f)
                                            .setDuration(IntConst.DURATION_500)
                                            .animate();
                                })
                                .animate();
                    }

                }


                activity.e("============selected================");

                if (selectedTab != position) {
                    showBar();
                }
                selectTab(position, false);

                if (position == 3) {

                } else {
                    setToolbarAlpha(0);
                }

                selectedTab = position;
                // 设置fab
                initFab();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void scaleFab(float faction) {
        fab.setScaleX(faction);
        fab.setScaleY(faction);
    }

    @Override
    public void initFab() {
        switch (selectedTab) {
            case 0:
                ((MsgFragment) adapter.getItem(selectedTab)).initFab();
                break;
            case 1:
                ((FriendsFragment) adapter.getItem(selectedTab)).initFab();
                break;
            case 2:
                ((RoutineFragment) adapter.getItem(selectedTab)).initFab();
                break;
            case 3:
                ((SelfFragment) adapter.getItem(selectedTab)).initFab();
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
        appbarLayout.setTranslationY(baseTransY + faction * distance);
        avatarView.setAlpha(faction);
        nameText.setAlpha(faction);
    }

    @Override
    public void initData() {
        avatarViewTransYDistance = IntConst.POSITIVE_120 * ScreenUtils.getDensity(getContext());
        avatarViewTransXDistance = IntConst.NAGETIVE_150 * ScreenUtils.getDensity(getContext());
        nameTextTransXDistance =  IntConst.POSITIVE_50* ScreenUtils.getDensity(getContext());
        btn = new LinearLayout[]{
                btnMsg,
                btnFriends,
                btnRoutine,
                btnSelf
        };
        iconsOpen = new int[]{
                R.drawable.ic_msg_open_24dp,
                R.drawable.ic_friends_open_24dp,
                R.drawable.ic_routine_open_24dp,
                R.drawable.ic_self_open_24dp
        };

        iconsClose = new int[]{
                R.drawable.ic_msg_close_24dp,
                R.drawable.ic_friends_close_24dp,
                R.drawable.ic_routine_close_24dp,
                R.drawable.ic_self_close_24dp
        };


        List<Fragment> fragments = generateFragments();
        adapter = new MainViewPagerAdapter(
                activity.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);


        // 为viewpager中的每个item设置相应的fab属性
        initFab();
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

    MsgFragment msgFragment = MsgFragment.newInstance(this);
    FriendsFragment friendsFragment = FriendsFragment.newInstance(this);
    RoutineFragment routineFragment = RoutineFragment.newInstance(this);
    SelfFragment selfFragment = SelfFragment.newInstance(this);

    @Override
    public List<Fragment> generateFragments() {
        msgFragment = MsgFragment.newInstance(this);
        friendsFragment = FriendsFragment.newInstance(this);
        routineFragment = RoutineFragment.newInstance(this);
        selfFragment = SelfFragment.newInstance(this);
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
    public MsgPresenter getMsgPresenter(MsgContract.View view) {

        return new MsgPresenter(view);
    }

    @Override
    public FriendsPresenter getFriendsPresenter(FriendsContract.View view) {

        return new FriendsPresenter(view);
    }

    @Override
    public RoutinePresenter getRoutinePresenter(RoutineContract.View view) {

        return new RoutinePresenter(view);
    }

    @Override
    public SelfPresenter getSelfPresenter(SelfContract.View view) {

        return new SelfPresenter(view);
    }


    @Override
    public void selectTab(int position, boolean setItem) {
        showNormalState();
        selectedTab = position;
        showSelectedState();
        if (setItem) {
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void showNormalState() {
        ((ImageView) btn[selectedTab]
                .getChildAt(0))
                .setImageResource(iconsClose[selectedTab]);
        ((TextView) btn[selectedTab]
                .getChildAt(1)).
                setTextColor(ContextCompat.getColor(getContext(), R.color.half_alpha_gray));
    }

    @Override
    public void showSelectedState() {

        ((ImageView) btn[selectedTab]
                .getChildAt(0))
                .setImageResource(iconsOpen[selectedTab]);
        ((TextView) btn[selectedTab]
                .getChildAt(1)).
                setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

    }

    private boolean isBarVisibale = true;

    @Override
    public void hideBar() {
        if (isBarVisibale) {
            isBarVisibale = false;
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            AnimUtils.just()
                    .setFloat(0, -appbarLayout.getHeight())
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onUpdateFloat(animator ->
                            appbarLayout.setTranslationY((Float) animator.getAnimatedValue()))
                    .onEnd(animator -> appbarLayout.setVisibility(View.GONE))
                    .animate();
            AnimUtils.just(bottomBar)
                    .setTranY(0, bottomBar.getHeight())
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onEnd(animator -> bottomBar.setVisibility(View.GONE))
                    .animate();
            AnimUtils.just(fab)
                    .setScaleX(0, 1f)
                    .setScaleY(0, 1f)
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onStart(animator -> fab.setVisibility(View.VISIBLE))
                    .animate();

        }

    }

    @Override
    public void showBar() {

        if (!isBarVisibale) {
            isBarVisibale = true;
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            AnimUtils.just()
                    .setFloat(0)
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onUpdateFloat(animator ->
                            appbarLayout.setTranslationY((Float) animator.getAnimatedValue()))
                    .onStart(animator -> appbarLayout.setVisibility(View.VISIBLE))
                    .animate();
            AnimUtils.just(bottomBar)
                    .setTranY(0)
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onStart(animator -> bottomBar.setVisibility(View.VISIBLE))
                    .animate();
            AnimUtils.just(fab)
                    .setScaleX(0)
                    .setScaleY(0)
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onEnd(animator -> fab.setVisibility(View.GONE))
                    .animate();
        }

    }


    int baseTransY = -(IntConst.HEAD_VIEW_HEIGHT - IntConst.TOOLBAR_HEIGHT);
    int distance = -baseTransY;
    boolean isHeadViewScrolledUp = false;
    boolean isActionMove = false;
    float oldY = 0;

    float avatarViewScaleTo = 0.32f;
    float avatarViewScaleDistance = 1-0.32f;
    float avatarViewTransYDistance;
    float avatarViewTransXDistance;
    float nameTextTransXDistance;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (selectedTab == 3 ) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldY = event.getY();
                    activity.e("down,y = "+ event.getY());
                    isActionMove = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    isActionMove = true;
                    activity.e("move,y = "+event.getY());
                    float deltaY = event.getY() - oldY;
                    oldY = event.getY();
                    float faction = getMoveFaction(deltaY);
                    moveHeadView(faction);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isActionMove) {
                        activity.e("up,y = "+event.getY());
                        float transY = -appbarLayout.getTranslationY();
                        if (transY > distance / 2) {
                            // 滑动超过一半
                            isHeadViewScrolledUp = true;
                            animateHeadView(1);

                        } else {
                            // 滑动未超过一半
                            isHeadViewScrolledUp = false;
                            animateHeadView(0);
                        }
                    }
                    break;
                default:
                    break;
            }
            ((SelfFragment) adapter.getItem(3)).onTouch(event);
        }

        return false;
    }

    // 获取当前headView应该滑动的百分比
    private float getMoveFaction(float deltaY) {
        float transY = appbarLayout.getTranslationY();
        transY += deltaY;
        if (deltaY < 0 && transY < baseTransY) {
            //向上滑动,并且appbarLayout还没有滑到最高上限
            transY = baseTransY;
        } else if (deltaY > 0 && transY > 0) {
            //向下滑动，并且appbarLayout还没有滑到最低下限
            transY = 0;
        }

        float faction = (-transY) / distance;

        return faction;
    }

    // 这个函数在手指滑动是调用
    private void moveHeadView(float faction) {
        appbarLayout.setTranslationY(-faction*distance);
        avatarView.setScaleX(1-faction*avatarViewScaleDistance);
        avatarView.setScaleY(1-faction*avatarViewScaleDistance);
        avatarView.setTranslationY(faction*avatarViewTransYDistance);
        avatarView.setTranslationX(faction*avatarViewTransXDistance);
        nameText.setTranslationX(faction * nameTextTransXDistance);
    }

    // 这个函数在手指松开是调用
    // 0 表示打开，1 表示关上
    private void animateHeadView(@IntRange(from = 0,to = 1) int faction) {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float startFaction = -(appbarLayout.getTranslationY()/distance);
        AnimUtils.just()
                .setDuration(IntConst.DURATION_500)
                .setInterpolator(interpolator)
                .setFloat(startFaction,faction)
                .onUpdateFloat(animator -> {
                    float factor = (float) animator.getAnimatedValue();
                    appbarLayout.setTranslationY(-factor*distance);
                    avatarView.setScaleX(1-factor*avatarViewScaleDistance);
                    avatarView.setScaleY(1-factor*avatarViewScaleDistance);
                    avatarView.setTranslationX(factor*avatarViewTransXDistance);
                    avatarView.setTranslationY(factor*avatarViewTransYDistance);
                    nameText.setTranslationX(factor * nameTextTransXDistance);
                })
                .animate();
    }

    @Override
    public int currentItemPosition() {
        return selectedTab;
    }
}
