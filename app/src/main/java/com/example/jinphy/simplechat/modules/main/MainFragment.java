package com.example.jinphy.simplechat.modules.main;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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

    private View appbarLayout;
    private Toolbar toolbar;
    private CardView headView;
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

        appbarLayout =  activity.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        headView = (CardView) activity.findViewById(R.id.head_view);
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

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                // position表示当前第一个可见的pager的位置，
                // offset表示position位置的pager的偏移百分比，正数
                // offsetPixels表示position位置的pager的偏移像素，正数
                // 当position 位置的pager位于正中间是，上面两个偏移量为0
                activity.e("position = "+position);
                activity.e("offset = "+offset);
                activity.e("offsetPixels = "+offsetPixels);
                activity.e("=============================================");


                switch (position) {
                    case 0:
                        if (isShowingHeadView) {
                            headView.setVisibility(View.GONE);
                            isShowingHeadView=false;
                        }
                        break;
                    case 1:
                        if (isShowingHeadView) {
                            headView.setVisibility(View.GONE);
                            isShowingHeadView=false;
                        }
                        break;
                    case 2: //当前可见的第一个pager为第三个fragment
                        if (offsetPixels < 5) {
                            if (isShowingHeadView) {
                                headView.setVisibility(View.GONE);
                                isShowingHeadView=false;
                            }
                        } else{
                            if (!isShowingHeadView) {
                                headView.setVisibility(View.VISIBLE);
                                isShowingHeadView=true;
                            }
                        }
                        toolbar.setTranslationX(-offsetPixels);
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageSelected(int position) {
                if (selectedTab != position) {
                    showBar();
                }
                selectTab(position,false);
                if (position == 3) {

                } else {
                    activity.e("============selected================");
                    toolbar.setTranslationX(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void initData() {
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
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(
                activity.getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }


    @Override
    public void selectFragment(View view) {
        switch (view.getId()) {
            case R.id.btn_msg:
                selectTab(0,true);
                break;
            case R.id.btn_friends:
                selectTab(1,true);
                break;
            case R.id.btn_routine:
                selectTab(2,true);
                break;
            case R.id.btn_self:
                selectTab(3,true);
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
    public void selectTab(int position,boolean setItem) {
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
                    .setFloat(0,-appbarLayout.getHeight())
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onUpdateFloat(animator ->
                        appbarLayout.setTranslationY((Float) animator.getAnimatedValue()))
                    .onEnd(animator -> appbarLayout.setVisibility(View.GONE))
                    .animate();
            AnimUtils.just(bottomBar)
                    .setTranY(0,bottomBar.getHeight())
                    .setInterpolator(interpolator)
                    .setDuration(IntConst.DURATION_500)
                    .onEnd(animator -> bottomBar.setVisibility(View.GONE))
                    .animate();
            AnimUtils.just(fab)
                    .setScaleX(0,1f)
                    .setScaleY(0,1f)
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


}
