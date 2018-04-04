package com.example.jinphy.simplechat.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.event_bus.EBNotificationEvent;
import com.example.jinphy.simplechat.models.event_bus.EBQRCodeContent;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.add_friend.AddFriendActivity;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.group.create_group.CreateGroupActivity;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupActivity;
import com.example.jinphy.simplechat.modules.group.group_list.GroupListActivity;
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
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements MainContract.View {

    public static final String FROM_LOGIN = "FROM_LOGIN";
    public static final String WITH_ACCOUNT = "WITH_ACCOUNT";

    private static final String TAG = "MainFragment";
    public static final int MSG_FRAGMENT = 0;
    public static final int FRIEND_FRAGMENT = 1;
    public static final int ROUTINE_FRAGMENT = 2;
    public static final int SELF_FRAGMENT = 3;

    public static final String SAVE_KEY_FROM_LOGIN = "SAVE_KEY_FROM_LOGIN";

    private ViewPager viewPager;

    MsgFragment msgFragment ;
    FriendsFragment friendsFragment ;
    RoutineFragment routineFragment;
    SelfFragment selfFragment;

    private View appbar;
    private Toolbar toolbar;

    private MainMenu menu;
    private MainBottomBar bottomBar;

    private Disposable disposable;

    private int screenWidth;
    private boolean fromLogin;

    /**
     * DESC: 当MainActivity是通过Notification启动时，传递的好友账号
     * Created by jinphy, on 2018/4/4, at 12:35
     */
    private String withAccount;

    public MainFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     *
     * @return A new app of fragment MainFragment.
     */
    public static MainFragment newInstance(boolean fromLogin,String withAccount) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainFragment fragment = new MainFragment();
        fragment.fromLogin = fromLogin;
        fragment.withAccount = withAccount;
        return fragment;
    }

    @Override
    public void initData() {
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        if (fromLogin) {
            presenter.loadDataAfterLogin();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            fromLogin = savedInstanceState.getBoolean(SAVE_KEY_FROM_LOGIN);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_KEY_FROM_LOGIN, fromLogin);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void findViewsById(View view) {
        MainActivity activity = (MainActivity) getActivity();

        appbar = activity.findViewById(R.id.appbar_layout);
        toolbar =  activity.findViewById(R.id.toolbar);

        viewPager = view.findViewById(R.id.view_pager);
        bottomBar = MainBottomBar.create(view.findViewById(R.id.bottom_bar));
        menu = MainMenu.create(
                LayoutInflater.from(activity).inflate(R.layout.menu_main_fragment, null));
    }

    @Override
    protected void setupViews() {
        viewPager.setAdapter(getAdapter());

        if (withAccount != null) {
            ChatActivity.start(activity(), withAccount);
            withAccount = null;
        }
    }

    @Override
    protected void registerEvent() {
        bottomBar.onClick(viewPager::setCurrentItem);

        menu.onClick(index -> {
            switch (index) {
                case 0:{
                    /**
                     * DESC: 添加好友
                     * Created by jinphy, on 2018/3/31, at 14:19
                     */
                    new MaterialDialog.Builder(activity())
                            .title("添加好友")
                            .titleColor(colorPrimary())
                            .icon(ImageUtil.getDrawable(activity(),R.drawable.ic_friends_open_24dp,colorPrimary()))
                            .content("输入简聊号")
                            .input("请输入需要添加的好友简聊号", null, (dialog, input) -> {
                                if (!StringUtils.isPhoneNumber(input.toString())) {
                                    App.showToast("请输入正确的账号！", false);
                                    return;
                                }

                                presenter.findUser(input.toString(),response->{
                                    if (response != null && Response.YES.equals(response.getCode())) {
                                        AddFriendActivity.start(activity(), response.getData());
                                    }
                                });
                                dialog.dismiss();
                            })
                            .cancelable(false)
                            .autoDismiss(false)
                            .positiveText("查找")
                            .negativeText("取消")
                            .positiveColor(colorPrimary())
                            .negativeColorRes(R.color.color_red_D50000)
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                    break;
                }
                case 1:{
                    /**
                     * DESC: 查找群聊
                     * Created by jinphy, on 2018/3/31, at 14:18
                     */
                    new MaterialDialog.Builder(activity())
                            .title("查找群聊")
                            .titleColor(colorPrimary())
                            .icon(ImageUtil.getDrawable(activity(),R.drawable.ic_friends_open_24dp,colorPrimary()))
                            .content("输入群号、群名")
                            .input("请输入群号（以'G'开头）或群名", null, (dialog, input) -> {
                                String text = input.toString();
                                if (TextUtils.isEmpty(text)) {
                                    App.showToast("输入不能为空！", false);
                                    return;
                                }

                                presenter.findGroups(text, ()->{
                                    GroupListActivity.start(activity(), true);
                                    dialog.dismiss();
                                });
                            })
                            .cancelable(false)
                            .autoDismiss(false)
                            .positiveText("查找")
                            .negativeText("取消")
                            .positiveColor(colorPrimary())
                            .negativeColorRes(R.color.color_red_D50000)
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                    break;
                }
                case 2:{
                    /**
                     * DESC: 发起群聊
                     * Created by jinphy, on 2018/3/31, at 14:18
                     */
                    CreateGroupActivity.start(activity());
                    break;
                }
                case 3:{
                    /**
                     * DESC: 扫一扫
                     * Created by jinphy, on 2018/3/31, at 14:19
                     */
                    QRCode.scan(activity(),content -> {
                        EventBus.getDefault().post(new EBQRCodeContent(content));
                    });
                    break;
                }
                default:
                    break;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                // position表示当前第一个可见的pager的位置，
                // offset表示position位置的pager的偏移百分比，正数
                // offsetPixels表示position位置的pager的偏移像素，正数
                // 当position 位置的pager位于正中间是，上面两个偏移量为0

                //处理appbar的动画
                handleBarOnViewPagerScrolled(position, offset, offsetPixels);

                // 第四页的动画处理
                selfFragment.handleOnViewPagerScrolled(position, offset, offsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.checkItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // no-op
            }
        });
    }

    //---------------私有函数---------------------------------------------

    private MainViewPagerAdapter getAdapter() {
        List<Fragment> fragments = generateFragments();
        FragmentManager manager = this.getChildFragmentManager();
        return new MainViewPagerAdapter(manager, fragments);
    }

    private void handleBarOnViewPagerScrolled(int position, float offset, int offsetPixels) {
        switch (position) {
            case 0:
                appbar.setTranslationX(0);
                break;
            case 1:
                appbar.setTranslationX(0);
                break;
            case 2:
                appbar.setTranslationX(-offsetPixels);
                break;
            case 3:
                appbar.setTranslationX(-screenWidth);
                break;
        }
    }

    // 获取一个fragment时，判断fragment是否存在于FragmentManager中，
    // 如果存在，在从FragmentManager中获取，否则生成一个新的fragment对象实例
    @SuppressWarnings("unchecked")
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

    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment,menu);
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                menu.show(activity(),toolbar);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        if (!menu.dismiss()) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return false;
    }


    //----------mvp中的View函数--------------------------------------------

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

        return new SelfPresenter(getContext(), (SelfContract.View) fragment);
    }

    @Override
    public boolean handleVerticalTouchEvent(MotionEvent event) {
        if (bottomBar.getCheckedIndex() == 3) {
            return selfFragment.handleVerticalTouchEvent(event);
        }
        return false;
    }

    @Override
    public int getCheckedTab() {
        return bottomBar.getCheckedIndex();
    }

    @Override
    public void showUserInfo() {
        ModifyUserActivity.start(activity());
    }

    @Override
    public void showFriendInfo(String account) {
        ModifyFriendInfoActivity.start(activity(), account);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleQRCodeContent(EBQRCodeContent msg) {
        QRCode.Content content = msg.data;
        if (content.isMyApp()) {
            switch (content.getType()) {
                case QRCode.Content.TYPE_GROUP:
                    Group group = presenter.getGroup(content.getText());
                    if (group != null) {
                        ModifyGroupActivity.start(activity(), group.getGroupNo());
                        return;
                    }
                    presenter.findGroups(content.getText(), ()->{
                        GroupListActivity.start(activity(), true);
                    });
                    break;
                case QRCode.Content.TYPE_USER:
                    presenter.findUser(content.getText(),response->{
                        if (response != null && Response.YES.equals(response.getCode())) {
                            AddFriendActivity.start(activity(), response.getData());
                        }
                    });
                    break;
                default:
            }
        } else {
            App.showToast("暂无法处理：" + content, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notificationEvent(EBNotificationEvent msg) {
        ChatActivity.start(activity(), msg.data);
    }
}
