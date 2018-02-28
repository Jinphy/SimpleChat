package com.example.jinphy.simplechat.modules.main.self;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.event_bus.EBUser;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.modules.login.LoginActivity;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.jinphy.simplechat.utils.ScreenUtils.dp2px;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class SelfFragment extends BaseFragment<SelfPresenter> implements SelfContract.View {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;


    private FloatingActionButton fab;

    private ScrollView scrollView;
    private CardView headView;
    private ImageView avatarView;
    private TextView nameText;
    private TextView dateText;
    private ImageView sexView;
    private TextView btnLogout;

    private float density;
    private User user;

    public SelfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     *
     * @return A new app of fragment FriendsFragment.
     */
    public static SelfFragment newInstance() {
        SelfFragment fragment = new SelfFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            presenter = getPresenter();
        }
    }

    @Override
    public void initFab(Activity activity) {
        fab = activity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setScaleX(1);
        fab.setScaleY(1);
        fab.setTranslationY(-ScreenUtils.getToolbarHeight(getContext()));
        fab.setOnClickListener(this::fabAction);
    }

    @Override
    public void fabAction(View view) {
        ModifyUserActivity.start(getActivity());

    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_self;
    }

    @Override
    public void initData() {
        density = ScreenUtils.getDensity(getContext());

        distanceVertical = dp2px(IntConst.HEAD_VIEW_HEIGHT - IntConst.TOOLBAR_HEIGHT,density);
        baseTransY = -distanceVertical;
        avatarViewTransXDistance = dp2px(IntConst.NEGATIVE_150, density);
        avatarViewTransYDistance = dp2px(IntConst.POSITIVE_120, density);
        nameTextTransXDistance = dp2px(IntConst.POSITIVE_50, density);
        sexViewTransXDistance = nameTextTransXDistance;
        user = presenter.getUser();
    }

    @Override
    protected void findViewsById(View view) {
        scrollView = view.findViewById(R.id.scroll_view);
        headView = view.findViewById(R.id.head_view);
        avatarView = headView.findViewById(R.id.avatar);
        nameText = headView.findViewById(R.id.name);
        dateText = headView.findViewById(R.id.date);
        sexView = headView.findViewById(R.id.sex);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    @Override
    protected void setupViews() {
        setupUser();
    }

    @Override
    public void setupUser() {
        nameText.setText(user.getName()==null?"没有昵称":user.getName());
        sexView.setVisibility(user.getSex() == null ? View.GONE : View.VISIBLE);
        sexView.setImageResource(getString(R.string.male).equals(user.getSex())
                ? R.drawable.ic_male : R.drawable.ic_female);
        dateText.setText(StringUtils.formatDate(user.getDate()));
        if (user.getAvatar() != null) {
            avatarView.setImageBitmap(StringUtils.base64ToBitmap(user.getAvatar()));
        }
    }

    @Override
    protected void registerEvent() {
        btnLogout.setOnClickListener(view->{
            if (activity() == null) {
                return;
            }
            new MaterialDialog.Builder(activity())
                    .title("退出登录")
                    .titleColor(colorPrimary())
                    .icon(ImageUtil.getDrawable(activity(),R.drawable.ic_account_24dp,colorPrimary()))
                    .content("退出后将无法再接收到消息\n是否继续？")
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText("是的")
                    .negativeText("不了")
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.half_alpha_gray)
                    .onPositive((dialog, which) -> {
                        presenter.logout(activity(), user.getAccount(),user.getAccessToken());
                    })
                    .show();

        });
    }


    @Override
    protected SelfPresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getSelfPresenter(this);
    }


    int distanceVertical;
    float avatarViewScaleDistance = 0.68f;
    float avatarViewTransXDistance;
    float avatarViewTransYDistance;
    float nameTextTransXDistance;
    float sexViewTransXDistance;
    int baseTransY;
    float oldY;

    boolean isHeadViewMoveUp = false;
    boolean hasActionMove = false;

    private int moveOrientation;

    private float deltaY;

    @Override
    public boolean handleVerticalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                moveOrientation = VERTICAL;
                hasActionMove = true;
                deltaY = event.getY() - oldY;
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
                if (hasActionMove && moveOrientation==VERTICAL) {
                    hasActionMove = false;
                    float factor = -headView.getTranslationY()*1.0f/distanceVertical;
                    if (deltaY < 0) {
                        // 向上滑动
                        if (factor > 1.0f / 4) {
                            // 收起
                            animateVertical(factor, 1);
                            isHeadViewMoveUp = true;
                        } else {
                            // 展开
                            animateVertical(factor, 0);
                            isHeadViewMoveUp = false;
                        }
                    } else {
                        //向下滑动
                        if (factor < 3.0f / 4) {
                            // 展开
                            animateVertical(factor, 0);
                            isHeadViewMoveUp = false;
                        } else {
                            // 收起
                            animateVertical(factor, 1);
                            isHeadViewMoveUp = true;
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }


    /**
     * DESC: 判断是否可以向上移动
     * Created by jinphy, on 2018/1/11, at 14:12
     */
    @Override
    public boolean canMoveUp() {
        float transY = -headView.getTranslationY();
        boolean can = transY< distanceVertical;
        return can;
    }

    /**
     * DESC: 判断是否可以向下移动
     * Created by jinphy, on 2018/1/11, at 14:12
     */
    @Override
    public boolean canMoveDown() {
        float transY = headView.getTranslationY();
        boolean can = transY < 0;

        int scrollY = scrollView.getScrollY();
        return can && scrollY == 0;
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
    @Override
    public void moveVertical(float faction) {
        Observable.just("check null")
                .map(this::checkViewNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isNull-> moveVertical(isNull,faction));
    }


    private void moveVertical(boolean isNull, float faction) {
        if (!isNull) {
            headView.setTranslationY(-faction* distanceVertical);
            // TODO: 2017/8/13 headView和statusBar背景颜色初始值设置为相应头像的Palette颜色，
            // TODO: 2017/8/13 headView的结束颜色设置为R.color.colorPrimary
            // TODO: 2017/8/13 statusBar的结束颜色设置为 R.color.colorPrimaryDark
            headView.setCardBackgroundColor(ColorUtils.rgbColorByFactor(
                    colorPrimaryDark(),
                    colorPrimary(),
                    faction
            ));
            scrollView.setTranslationY(distanceVertical *(1-faction));
            ViewUtils.setScaleXY(avatarView,1-faction*avatarViewScaleDistance);
            avatarView.setTranslationX(faction*avatarViewTransXDistance);
            avatarView.setTranslationY(faction*avatarViewTransYDistance);
            nameText.setTranslationX(faction*nameTextTransXDistance);
            sexView.setTranslationX(faction * sexViewTransXDistance);
            dateText.setAlpha(1-faction);
        }
    }

    /**
     * DESC: 手指松开后的动画
     * Created by jinphy, on 2018/1/11, at 14:15
     */
    @Override
    public void animateVertical(float fromFactor,float toFactor) {
        float deltaFactor = Math.abs(toFactor - fromFactor);
        AnimUtils.just()
                .setDuration((long) (IntConst.DURATION_250*deltaFactor))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setFloat(fromFactor,toFactor)
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    moveVertical(value);
                })
                .animate();
    }


    /**
     * DESC: 获取当前headView竖直方向上滑动的百分比
     * Created by jinphy, on 2018/1/11, at 14:14
     */
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

        float faction = transY / distanceVertical;

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
                    moveVertical(1);
                    ViewUtils.setAlpha(offset,avatarView,nameText);
                } else {
                    moveVertical(1-offset);
                }
                break;
            case 3:
                break;
            default:
                break;

        }
    }

    /**
     * DESC: 当修改用户信息是该函数将会接收到修改结果
     * Created by jinphy, on 2018/1/11, at 13:17
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void whenModifyUserInfo(EBUser msg) {
        if (msg.ok) {
            user = msg.data;
            setupUser();
        }
    }

    @Override
    public void whenLogout() {
        App.showToast("账号已退出！", false);
        LoginActivity.start(activity());
        finishActivity();
    }
}
