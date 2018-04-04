package com.example.jinphy.simplechat.modules.modify_user_info;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.RuntimePermission;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.event_bus.EBUser;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.User_;
import com.example.jinphy.simplechat.modules.pick_photo.PickPhotoActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.EditUtils;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import io.objectbox.Property;

/**
 * DESC: 修改用户信息fragment
 * Created by jinphy, on 2018/1/7, at 15:50
 */
public class ModifyUserFragment extends BaseFragment<ModifyUserPresenter> implements
        ModifyUserContract.View {

    private FloatingActionButton fabSave;
    private FloatingActionButton fabCancel;
    private View activityRootView;
    private View activityBottomLayout;

    private MenuItemView avatarItem;
    private MenuItemView accountItem;
    private MenuItemView nameItem;
    private MenuItemView statusItem;
    private MenuItemView signatureItem;
    private MenuItemView passwordItem;
    private MenuItemView dateItem;
    private MenuItemView sexItem;
    private MenuItemView addressItem;


    private User user;
    private Bitmap avatarBitmap;
    private Bitmap oldAvatarBitmap;
    private boolean exitAfterModifyUserInfo = false;
    private String deviceId;

    public ModifyUserFragment() {
        // Required empty public constructor
    }

    public static ModifyUserFragment newInstance() {
        ModifyUserFragment fragment = new ModifyUserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_modify_user;
    }

    @Override
    protected void initData() {
        RuntimePermission.newInstance(activity())
                .permission(Manifest.permission.READ_PHONE_STATE)
                .onGranted(() -> {
                    deviceId = EncryptUtils.md5(DeviceUtils.deviceId());
                })
                .onReject(() -> App.showToast("您拒绝了访问手机状态的权限！", false))
                .execute();
        user = presenter.getUser();
    }

    @Override
    protected void findViewsById(View view) {
        fabSave = getActivity().findViewById(R.id.fab_save);
        fabCancel = getActivity().findViewById(R.id.fab_cancel);
        activityRootView = getActivity().findViewById(R.id.root_view);
        activityBottomLayout = getActivity().findViewById(R.id.bottom_layout);

        avatarItem = view.findViewById(R.id.item_avatar);
        accountItem = view.findViewById(R.id.item_account);
        nameItem = view.findViewById(R.id.item_name);
        statusItem = view.findViewById(R.id.item_status);
        signatureItem = view.findViewById(R.id.item_signature);
        passwordItem = view.findViewById(R.id.item_password);
        dateItem = view.findViewById(R.id.item_date);
        sexItem = view.findViewById(R.id.item_sex);
        addressItem = view.findViewById(R.id.item_address);
    }

    @Override
    protected void setupViews() {
        oldAvatarBitmap = avatarBitmap = StringUtils.base64ToBitmap(user.getAvatar());
        if (avatarBitmap != null) {
            avatarItem.iconDrawable(new BitmapDrawable(getResources(), avatarBitmap));
        }
        accountItem.content(user.getAccount());
        nameItem.content(user.getName() == null ? "" : user.getName());
        statusItem.content(User.STATUS_LOGIN.equals(user.getStatus()) ? "在线" : "离线");
        signatureItem.content(user.getSignature() == null ? "" : user.getSignature());
        passwordItem.content(user.getPassword() == null ? "" : EncryptUtils.aesDecrypt(user.getPassword()));
        dateItem.content(StringUtils.formatTime(user.getDate()));
        sexItem.content(user.getSex() == null ? "" : user.getSex());
        addressItem.content(user.getAddress() == null ? "" : user.getAddress());
    }

    @Override
    protected void registerEvent() {
        avatarItem.onClick((menuItemView, hasFocus) -> {
            pickPhoto();
        });
        // 密码的点击事件
        passwordItem.onClick((menuItemView, hasFocus) -> {
            if (user.getPassword() == null) {
                modifyPasswordWithCode();
            } else {
                modifyPasswordWithOld();
            }
        });
        sexItem.onClick((menuItemView, hasFocus) -> {
            selectSex();
        });
        fabCancel.setOnClickListener(view -> onBackPressed());
        fabSave.setOnClickListener(view -> {
            if (!hasModified()) {
                App.showToast("您未进行任何信息修改，无需保存！", false);
            } else {
                modifyUserInfo();
            }
        });
    }

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (open) {
            // 键盘打开
            animateBottomLayout(1, 0);
        } else {
            // 键盘关闭
            animateBottomLayout(0, 1);
            MenuItemView.removeCurrent();
        }
    }

    private static final String TAG = "ModifyUserFragment";

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (hasModified() && activity() != null) {
            new MaterialDialog.Builder(activity())
                    .title("提示")
                    .titleColor(colorPrimary())
                    .icon(ImageUtil.getDrawable(activity(),
                            R.drawable.ic_warning_24dp, colorPrimary()))
                    .content("您的个人信息已修改\n退出前是否保存！")
                    .positiveText("是")
                    .negativeText("否")
                    .neutralText("取消")
                    .cancelable(false)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.color_red_D50000)
                    .neutralColorRes(R.color.half_alpha_gray)
                    .onPositive((dialog, which) -> {
                        // 保存个人信息并退出
                        modifyUserInfo();
                        exitAfterModifyUserInfo = true;
                    })
                    .onNegative((dialog, which) -> {
                        // 不保存个人信息且退出
                        finishActivity();
                    })
                    .onNeutral((dialog, which) -> {
                        // 取消退出，无操作
                    })
                    .show();

        } else {
            finishActivity();
        }

        // 返回false，则activity将会不回调onBackPressed
        return false;
    }


    /**
     * DESC: 执行底部栏的动画
     * Created by jinphy, on 2018/1/8, at 21:53
     */
    private void animateBottomLayout(int fromAlpha, int toAlpha) {
        AnimUtils.just(activityBottomLayout)
                .setAlpha(fromAlpha, toAlpha)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .onEnd(animator -> {
                    if (toAlpha == 0) {
                        activityBottomLayout.setVisibility(View.GONE);
                    } else {
                        activityBottomLayout.setVisibility(View.VISIBLE);
                    }
                })
                .animate();
    }


    private String tempPassword;

    @Override
    public void modifyPasswordWithCode() {
        BaseActivity context = (BaseActivity) getActivity();
        if (context == null) {
            return;
        }
        // 提示用户是否发送验证码
        new MaterialDialog.Builder(context)
                .title("提示")
                .icon(ImageUtil.getDrawable(context, R.drawable.ic_warning_24dp,colorPrimary()))
                .content("您是通过验证登录，\n修改密码前请重新验证！")
                .contentGravity(GravityEnum.CENTER)
                .positiveText("好的")
                .titleColor(colorPrimary())
                .positiveColor(colorPrimary())
                .cancelable(true)
                .negativeText("不了")
                .negativeColorRes(R.color.color_red_D50000)
                .onPositive((dialog, which) -> {
                    // 获取验证码
                    presenter.getCode(
                            context,
                            user.getAccount(),
                            response -> {
                                // 获取验证码成功
                                App.showToast(response.getMsg(), false);

                                /**
                                 * DESC: 发送验证码后分三步
                                 *
                                 * 1、提交验证码
                                 * 2、输入新密码
                                 * 3、提交验证码
                                 * Created by jinphy, on 2018/1/9, at 18:10
                                 */
                                new MaterialDialog.Builder(context)
                                        .title("修改密码")
                                        .titleColor(colorPrimary())
                                        .iconRes(R.drawable.ic_lock_outline_24dp)
                                        .content("输入验证码")
                                        .autoDismiss(false)
                                        .cancelable(false)
                                        .negativeText("取消")
                                        .positiveColor(colorPrimary())
                                        .onNegative((dialog12, which1) -> {
                                            dialog12.dismiss();
                                        })
                                        .negativeColor(ContextCompat.getColor(context, R.color.color_red_D50000))
                                        .inputType(InputType.TYPE_CLASS_NUMBER)
                                        .input("请输入验证码！", "", (dialog1, input) -> {
                                            EditText inputEditText = dialog1.getInputEditText();
                                            switch (inputEditText.getHint().toString()) {
                                                case "请输入验证码！":
                                                    if (ObjectHelper.isTrimEmpty(input)) {
                                                        App.showToast("请输入验证码！", false);
                                                        return;
                                                    }
                                                    // 提交验证码
                                                    presenter.submitCode(
                                                            context,
                                                            user.getAccount(),
                                                            input.toString(),
                                                            response1 -> {
                                                                // 提交码验证成功
                                                                App.showToast(response1.getMsg(), false);
                                                                dialog1.setContent("输入新密码");
                                                                inputEditText.setHint("请输入新密码！");
                                                                inputEditText.setText("");
                                                                EditUtils.textPassword(inputEditText);
                                                                Keyboard.open(context, inputEditText);
                                                            });
                                                    break;
                                                case "请输入新密码！":
                                                    if (ObjectHelper.isTrimEmpty(input) || input
                                                            .length() < 6) {
                                                        App.showToast("请输入至少6位密码！", false);
                                                        return;
                                                    }
                                                    tempPassword = input.toString();
                                                    dialog1.setContent("确认密新码");
                                                    inputEditText.setHint("请再次输入相同的密码！");
                                                    inputEditText.setText("");
                                                    break;
                                                case "请再次输入相同的密码！":
                                                    if (StringUtils.notEqual(tempPassword, input
                                                            .toString())) {
                                                        App.showToast("两次密码输入不一致！", false);
                                                        return;
                                                    }
                                                    passwordItem.content(tempPassword);
                                                    dialog1.dismiss();
                                                    App.showToast("密码已修改！", false);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        })
                                        .show();
                            });
                })
                .show();

    }

    @Override
    public void modifyPasswordWithOld() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        new MaterialDialog.Builder(context)
                .title("修改密码")
                .titleColor(colorPrimary())
                .iconRes(R.drawable.ic_lock_outline_24dp)
                .content("输入旧密码")
                .autoDismiss(false)
                .cancelable(false)
                .negativeText("取消")
                .positiveColor(colorPrimary())
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                })
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .negativeColor(ContextCompat.getColor(context, R.color.color_red_D50000))
                .input("请输入旧密码！", "", (dialog1, input) -> {
                    EditText inputEditText = dialog1.getInputEditText();
                    switch (inputEditText.getHint().toString()) {
                        case "请输入旧密码！":
                            if (StringUtils.notEqual(EncryptUtils.aesDecrypt(user.getPassword()),
                                    input.toString())) {
                                App.showToast("密码不正确，请重新输入！", false);
                                return;
                            }
                            dialog1.setContent("输入新密码");
                            inputEditText.setHint("请输入新密码！");
                            inputEditText.setText("");
                            break;
                        case "请输入新密码！":
                            if (ObjectHelper.isTrimEmpty(input) || input.length() < 6) {
                                App.showToast("请输入至少6位密码！", false);
                                return;
                            }
                            tempPassword = input.toString();
                            dialog1.setContent("确认新密码");
                            inputEditText.setHint("请再次输入相同的密码！");
                            inputEditText.setText("");
                            break;
                        case "请再次输入相同的密码！":
                            if (StringUtils.notEqual(tempPassword, input.toString())) {
                                App.showToast("两次密码输入不一致！", false);
                                return;
                            }
                            passwordItem.content(tempPassword);
                            dialog1.dismiss();
                            App.showToast("密码已修改！", false);
                            LogUtils.e(passwordItem.content());
                            break;
                        default:
                            break;
                    }
                })
                .show();

    }

    @Override
    public void selectSex() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        new MaterialDialog.Builder(context)
                .title("性别")
                .titleColor(colorPrimary())
                .iconRes(R.drawable.ic_flower_24dp)
                .items(getString(R.string.male), getString(R.string.female))
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    if (!TextUtils.isEmpty(text)) {
                        sexItem.content(text);
                        dialog.dismiss();
                        return true;
                    }
                    App.showToast("请选择是否自动添加新成员！", false);
                    return false;
                })
                .widgetColor(colorPrimary())
                .positiveText("确定")
                .negativeText("取消")
                .autoDismiss(false)
                .cancelable(false)
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .onNegative((dialog, which) -> dialog.dismiss())
                .dismissListener(dialog -> MenuItemView.removeCurrent())
                .show();
    }

    @Override
    public void pickPhoto() {
        if (activity() == null) {
            return;
        }
        new MaterialDialog.Builder(activity())
                .title("头像")
                .titleColor(colorPrimary())
                .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_photo_24dp,
                        colorPrimary()))
                .content("请选择图片获取方式！")
                .positiveText("相机")
                .negativeText("相册")
                .neutralText("取消")
                .contentGravity(GravityEnum.CENTER)
                .positiveColor(colorPrimary())
                .negativeColor(colorAccent())
                .neutralColorRes(R.color.color_red_D50000)
                .onPositive((dialog, which) -> {
                    // 从相机获取图片
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.TAKE_PHOTO,TAG);
                })
                .onNegative((dialog, which) -> {
                    // 从相册获取图片
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.CHOOSE_PHOTO,TAG);
                })
                .dismissListener(dialog -> MenuItemView.removeCurrent())
                .show();
    }

    /**
     * DESC: 获取图片的结果
     * Created by jinphy, on 2018/1/10, at 21:11
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pickPhotoResult(EBBitmap result) {
        if (!TAG.equals(result.tag)) {
            return;
        }
        if (result.ok) {
            // 图片获取成功
            avatarBitmap = result.data;
            oldAvatarBitmap = null;
            avatarItem.iconDrawable(new BitmapDrawable(getResources(), avatarBitmap));
        } else {
            // 图片获取失败
            // no-op
        }
    }

    /**
     * DESC: 判断头像是否有更换过
     * Created by jinphy, on 2018/1/11, at 11:08
     */
    private boolean modifiedAvatar() {
        if (avatarBitmap!=null && avatarBitmap != oldAvatarBitmap) {
            return true;
        }
        return false;
    }



    /**
     * DESC: 判断除了头像之外的其他信息是否有修改过
     * Created by jinphy, on 2018/1/11, at 11:08
     */
    private boolean modifiedColumn(Property column) {
        if (column == User_.name) {
            return StringUtils.notEqualNullable(nameItem.content().toString(), user.getName());
        } else if (column == User_.signature) {
            return StringUtils.notEqualNullable(
                    signatureItem.content().toString(), user.getSignature());
        } else if (column == User_.password) {
            return StringUtils.notEqualNullable(
                    passwordItem.content().toString(), EncryptUtils.aesDecrypt(user.getPassword()));
        } else if (column == User_.sex) {
            return StringUtils.notEqualNullable(sexItem.content().toString(), user.getSex());
        } else if (column == User_.address) {
            return StringUtils.notEqualNullable(addressItem.content().toString(), user.getAddress());
        }
        return false;
    }


    /**
     * DESC: 判断是否有信息被修改过
     * Created by jinphy, on 2018/1/11, at 11:07
     */
    private boolean hasModified() {
        return modifiedAvatar()
                || modifiedColumn(User_.name)
                || modifiedColumn(User_.signature)
                || modifiedColumn(User_.password)
                || modifiedColumn(User_.sex)
                || modifiedColumn(User_.address);
    }

    /**
     * DESC: 提交个人信息修改到服务器
     * Created by jinphy, on 2018/1/11, at 11:07
     */
    private void modifyUserInfo() {
        Map<String, Object> params = newParams()
                .add(Api.Key.account, user.getAccount())
                .add(Api.Key.accessToken, user.getAccessToken())
                .add(Api.Key.deviceId, deviceId)
                .add(Api.Key.avatar, modifiedAvatar()
                        ? StringUtils.bitmapToBase64(avatarBitmap)
                        : null)
                .add(Api.Key.name, modifiedColumn(User_.name) ? nameItem.content() : null)
                .add(Api.Key.signature, modifiedColumn(User_.signature)
                        ? signatureItem.content()
                        : null)
                .add(Api.Key.password, modifiedColumn(User_.password)
                        ? EncryptUtils.md5(passwordItem.content().toString())
                        : null)
                .add(Api.Key.sex, modifiedColumn(User_.sex) ? sexItem.content() : null)
                .add(Api.Key.address, modifiedColumn(User_.address) ? addressItem.content() : null)
                .build();

        presenter.modifyUserInfo(activity(),params);
    }

    @Override
    public void whenModifyUserInfoSucceed() {
        App.showToast("个人信息修改成功！", false);
        user = presenter.getUser();
        EventBus.getDefault().post(new EBUser(true, user));
        if (exitAfterModifyUserInfo) {
            finishActivity();
        } else {
            setupViews();
        }
    }

    /**
     * DESC: 获取修改过的密码，如果没有修改则为null
     * Created by jinphy, on 2018/1/11, at 11:27
     */
    @Override
    public String getModifiedPasswordAES() {
        if (modifiedColumn(User_.password)) {
            return EncryptUtils.aesEncrypt(passwordItem.content().toString());
        }
        return null;
    }

    @Override
    protected void finishActivity() {
        super.finishActivity();
        activity().overridePendingTransition(R.anim.anim_no, R.anim.alpha_out);
    }
}
