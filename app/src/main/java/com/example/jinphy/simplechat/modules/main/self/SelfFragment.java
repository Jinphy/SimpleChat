package com.example.jinphy.simplechat.modules.main.self;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.qr_code.QRCode;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.models.event_bus.EBUser;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.modules.login.LoginActivity;
import com.example.jinphy.simplechat.modules.main.MainFragment;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.DialogUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.example.jinphy.simplechat.utils.ScreenUtils.dp2px;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class SelfFragment extends BaseFragment<SelfPresenter> implements SelfContract.View {

    private User user;

    private SelfView myView;

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
    protected int getResourceId() {
        return R.layout.fragment_self;
    }

    @Override
    public void initData() {
        user = presenter.getUser();
    }

    @Override
    protected void findViewsById(View view) {
        myView = SelfView.init(this, view);
    }

    @Override
    protected void setupViews() {
        setupUser();
    }

    @Override
    public void setupUser() {
        myView.nameText.setText(user.getName()==null?"没有昵称":user.getName());
        myView.sexView.setVisibility(user.getSex() == null ? View.GONE : View.VISIBLE);
        myView.sexView.setImageResource(getString(R.string.male).equals(user.getSex())
                ? R.drawable.ic_male : R.drawable.ic_female);
        myView.dateText.setText(StringUtils.formatTime(user.getDate()));
        if (user.getAvatar() != null) {
            Bitmap avatar = StringUtils.base64ToBitmap(user.getAvatar());
            myView.avatarView.setImageBitmap(avatar);
            myView.blurBackground.setImageBitmap(
                    ImageUtil.blurBitmap(ImageUtil.copytBitmap(avatar),17));
        }
    }

    @Override
    protected void registerEvent() {
        myView.btnLogout.setOnClickListener(view->{
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
        myView.avatarView.setOnClickListener(v -> {
            DialogUtils.showQRCode(
                    activity(),
                    QRCode.content()
                            .setType(QRCode.Content.TYPE_USER)
                            .setText(user.getAccount())
                            .toString(),
                    user.getAccount(),
                    user.getName(),
                    "扫一扫，加我简聊"
            );
        });

        myView.menu.onClick(index -> {
            switch (index) {
                case 0:{ // 修改用户信息
                    ModifyUserActivity.start(activity());
                    return;
                }
                case 1:{ // 关于简聊
                    MyDialog.Holder dialogHolder = MyDialog.create(activity())
                            .view(R.layout.layout_about_app_dialog)
                            .width(300)
                            .cancelable(false)
                            .display();
                    dialogHolder.view.findViewById(R.id.btn_confirm)
                            .setOnClickListener(v -> {
                                dialogHolder.dialog.dismiss();
                            });
                    TextView contentView = dialogHolder.view.findViewById(R.id.content_view);
                    String content = contentView.getText().toString();
                    String[] wheres = {"简介：", "免责声明："};
                    SChain.with(content)
                            .colorForText(colorPrimary(), wheres)
                            .sizeRelative(1.1f, wheres)
                            .into(contentView);
                    break;
                }
                default:
                    break;

            }

        });
    }


    @Override
    protected SelfPresenter getPresenter() {
        MainFragment parentFragment = (MainFragment) getParentFragment();
        return parentFragment.getSelfPresenter(this);
    }


    @Override
    public boolean handleVerticalTouchEvent(MotionEvent event) {
        if (myView != null) {
            return myView.handleVerticalTouchEvent(event);
        }
        return super.handleVerticalTouchEvent(event);
    }


    @Override
    public void handleOnViewPagerScrolled(int position, float offset, int offsetPixels) {
        if (myView != null) {
            myView.onViewPagerScrolled(position, offset, offsetPixels);
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
