package com.example.jinphy.simplechat.modules.group.group_detail;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.SChain.Consumer;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.event_bus.EBMessage;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.group.member_list.MemberListActivity;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.modules.pick_photo.PickPhotoActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * DESC:
 * Created by jinphy, on 2018/3/12, at 9:27
 */
public class ModifyGroupFragment extends BaseFragment<ModifyGroupPresenter> implements ModifyGroupContract.View {

    public static final String TAG = "ModifyGroupFragment";
    public static final String GROUP_NO = "SAVE_KEY_GROUP_NO";
    public static final String AUTO = "自动加入";
    public static final String NO_AUTO = "群主验证";
    public static final String SAVE_KEY_GROUP_NO = "SAVE_KEY_GROUP_NO";

    private String groupNo;

    private MenuItemView avatarItem;
    private MenuItemView nameItem;
    private MenuItemView groupNoIem;
    private MenuItemView creatorItem;
    private MenuItemView membersItem;
    private MenuItemView maxCountItem;
    private MenuItemView autoAddItem;
    private MenuItemView extraMsgItem;
    private MenuItemView showMemberNameItem;
    private MenuItemView keepSilentItem;
    private MenuItemView rejectMsgItem;
    private Group group;
    private Bitmap bitmap;

    private View bottomView;
    private View btnBreakGroup;
    private View btnExitGroup;
    private View btnChat;
    private View btnAdd;
    private View btnDeleteLocal;


    public ModifyGroupFragment() {
        // Required empty public constructor
    }

    /**
     * DESC:
     * Created by jinphy, on 2018/3/12, at 9:27
     */
    public static ModifyGroupFragment newInstance(String groupNo) {
        ModifyGroupFragment fragment = new ModifyGroupFragment();
        fragment.groupNo = groupNo;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            groupNo = savedInstanceState.getString(SAVE_KEY_GROUP_NO);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_KEY_GROUP_NO, groupNo);
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_modify_group;
    }

    @Override
    protected void initData() {
        group = presenter.getGroup(groupNo);
    }

    @Override
    protected void findViewsById(View view) {
        avatarItem = view.findViewById(R.id.item_avatar);
        nameItem = view.findViewById(R.id.item_name);
        groupNoIem = view.findViewById(R.id.item_group_no);
        creatorItem = view.findViewById(R.id.item_creator);
        membersItem = view.findViewById(R.id.item_members);
        maxCountItem = view.findViewById(R.id.item_max_count);
        autoAddItem = view.findViewById(R.id.item_auto_add);
        extraMsgItem = view.findViewById(R.id.item_extra_msg);
        showMemberNameItem = view.findViewById(R.id.item_show_member_name);
        keepSilentItem = view.findViewById(R.id.item_keep_silent);
        rejectMsgItem = view.findViewById(R.id.item_reject_msg);

        bottomView = view.findViewById(R.id.bottom_layout);
        btnBreakGroup = view.findViewById(R.id.btn_break_group);
        btnExitGroup = view.findViewById(R.id.btn_exit_group);
        btnChat = view.findViewById(R.id.btn_chat);
        btnAdd = view.findViewById(R.id.btn_add_group);
        btnDeleteLocal = view.findViewById(R.id.btn_delete_local);
    }

    @Override
    protected void setupViews() {
        if (group == null) {
            return;
        }
        if (!group.isMyGroup()) {
            // 还未加入该群
            avatarItem.contentHint("");
            avatarItem.showArrow(false);
            avatarItem.autoAnimate(false);
            nameItem.showArrow(false);
            nameItem.autoAnimate(false);
            nameItem.showInput(false);
            creatorItem.showArrow(false);
            membersItem.setVisibility(View.GONE);
            maxCountItem.setVisibility(View.GONE);
            showMemberNameItem.setVisibility(View.GONE);
            keepSilentItem.setVisibility(View.GONE);
            rejectMsgItem.setVisibility(View.GONE);
            if (!group.isAutoAdd()&& !group.isBroke()) {
                extraMsgItem.setVisibility(View.VISIBLE);
            }
            if (group.isBroke()) {
                btnDeleteLocal.setVisibility(View.VISIBLE);
                avatarItem.content("该群已被群主解散");
            } else {
                if (!group.isFromSearch()) {
                    btnDeleteLocal.setVisibility(View.VISIBLE);
                    avatarItem.content("您已被群主移出该群");
                }
                btnAdd.setVisibility(View.VISIBLE);
            }

        } else {
            if (group.isCreator()) {
                // 是群主
                maxCountItem.showInput(true);
                maxCountItem.showArrow(true);
                maxCountItem.autoAnimate(true);
                autoAddItem.showArrow(true);
                autoAddItem.autoAnimate(true);

                btnBreakGroup.setVisibility(View.VISIBLE);
            } else {
                btnExitGroup.setVisibility(View.VISIBLE);
            }
            btnChat.setVisibility(View.VISIBLE);
        }


        bitmap = ImageUtil.loadAvatar(groupNo, 500, 500);
        if (bitmap != null) {
            avatarItem.iconDrawable(new BitmapDrawable(getResources(), bitmap));
        }
        nameItem.content(group.getName());
        groupNoIem.content(groupNo);
        creatorItem.content(group.getCreator());
        maxCountItem.content(group.getMaxCount() + "");
        membersItem.contentHint("点击查看（" + presenter.countMembers(group) + "）");
        autoAddItem.content(group.isAutoAdd() ? AUTO : NO_AUTO);
        showMemberNameItem.content(group.isShowMemberName() ? "是" : "否");
        keepSilentItem.content(group.isKeepSilent() ? "是" : "否");
        rejectMsgItem.content(group.isRejectMsg() ? "是" : "否");
    }

    @Override
    protected void registerEvent() {
        avatarItem.onClick((menuItemView, hasFocus) -> {
            pickPhoto();
        });
        nameItem.onReleaseFocus(() -> {
            String name = nameItem.content().toString();
            if (TextUtils.isEmpty(nameItem.content())) {
                App.showToast("群聊名字不能为空！", false);
                nameItem.content(group.getName());
            } else if (StringUtils.notEqual(name, group.getName())) {
                presenter.modifyName(activity(), group, name);
            }
        });
        creatorItem.onClick((menuItemView, hasFocus) -> {
            if (StringUtils.equal(group.getCreator(), group.getOwner())) {
                ModifyUserActivity.start(activity());
            } else {
                ModifyFriendInfoActivity.start(activity(), group.getCreator());
            }
        });
        membersItem.onClick((menuItemView, hasFocus) -> {
            MemberListActivity.start(activity(), groupNo);
        });
        maxCountItem.onReleaseFocus(() -> {
            String s = maxCountItem.content().toString();
            if (TextUtils.isEmpty(s)) {
                App.showToast("最大成员数不能为空！", false);
                maxCountItem.content(group.getMaxCount() + "");
                return;
            }
            if (!StringUtils.isNumber(s)) {
                App.showToast("请输入数字！", false);
                maxCountItem.content(group.getMaxCount() + "");
                return;
            }
            Integer maxCount = Integer.valueOf(s);
            int currentMemberCount = presenter.countMembers(group);

            if (maxCount <= currentMemberCount || maxCount > Group.DEFAULT_MAX_COUNT) {
                App.showToast(
                        "当前成员数：" +
                                SChain.with(currentMemberCount+"").colorForText(Color.GREEN).make()
                                + "\n" +
                                "请确保输入范围：" +
                                SChain.with((currentMemberCount + 1) + "~"
                                        + Group.DEFAULT_MAX_COUNT)
                                        .colorForText(Color.YELLOW).make(),
                        false);
                maxCountItem.content(group.getMaxCount() + "");
                return;
            }
            presenter.modifyMaxCount(activity(), group, maxCount);
        });
        autoAddItem.onClick((menuItemView, hasFocus) -> {
            setAutoAdd();
        });

        showMemberNameItem.onClick((menuItemView, hasFocus) -> {
            setShowMemberName();
        });
        keepSilentItem.onClick((menuItemView, hasFocus) -> {
            setKeepSilent();
        });
        rejectMsgItem.onClick((menuItemView, hasFocus) -> {
            setRejectMsg();
        });

        btnBreakGroup.findViewById(R.id.break_group).setOnClickListener(v -> {
            new MaterialDialog.Builder(activity())
                    .title("解除群聊")
                    .titleColor(colorPrimary())
                    .content("您将解散该群聊，并且操作不可恢复，是否继续？")
                    .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                    .positiveText("确定")
                    .negativeText("不了")
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.color_red_D50000)
                    .onPositive((dialog, which) -> {
                        presenter.breakGroup(activity(), group);
                    })
                    .show();

        });
        btnExitGroup.findViewById(R.id.exit_group).setOnClickListener(v->{
            new MaterialDialog.Builder(activity())
                    .title("退出群聊")
                    .titleColor(colorPrimary())
                    .content("您将退出该群聊，并且操作不可恢复，是否继续？")
                    .contentGravity(GravityEnum.CENTER)
                    .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                    .positiveText("确定")
                    .negativeText("不了")
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.color_red_D50000)
                    .onPositive((dialog, which) -> {
                        presenter.exitGroup(activity(), group);
                    })
                    .show();
        });
        btnChat.findViewById(R.id.chat).setOnClickListener(v -> {
            ChatActivity.start(activity(), groupNo);
        });
        btnAdd.findViewById(R.id.add_group).setOnClickListener(v -> {
            presenter.joinGroup(activity(), group, extraMsgItem.content().toString());
        });
        btnDeleteLocal.findViewById(R.id.delete_local).setOnClickListener(v -> {
            new MaterialDialog.Builder(activity())
                    .title("删除群聊")
                    .titleColor(colorPrimary())
                    .content("您将从本地删除该解散的群聊，是否继续？")
                    .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                    .positiveText("确定")
                    .negativeText("不了")
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.color_red_D50000)
                    .onPositive((dialog, which) -> {
                        presenter.deleteGroupLocal(group);
                    })
                    .show();
        });
    }


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
        finishActivity();
//        new MaterialDialog.Builder(activity())
//                .title("提示")
//                .titleColor(colorPrimary())
//                .icon(ImageUtil.getDrawable(activity(),
//                        R.drawable.ic_warning_24dp, colorPrimary()))
//                .content("您的个人信息已修改\n退出前是否保存！")
//                .positiveText("是")
//                .negativeText("否")
//                .neutralText("取消")
//                .cancelable(false)
//                .contentGravity(GravityEnum.CENTER)
//                .positiveColor(colorPrimary())
//                .negativeColorRes(R.color.color_red_D50000)
//                .neutralColorRes(R.color.half_alpha_gray)
//                .onPositive((dialog, which) -> {
//                    // 保存个人信息并退出
//                    modifyUserInfo();
//                    exitAfterModifyUserInfo = true;
//                })
//                .onNegative((dialog, which) -> {
//                    // 不保存个人信息且退出
//                    finishActivity();
//                })
//                .onNeutral((dialog, which) -> {
//                    // 取消退出，无操作
//                })
//                .show();

        // 返回false，则activity将会不回调onBackPressed
        return false;
    }

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {
            MenuItemView.removeCurrent();
            bottomView.setVisibility(View.VISIBLE);
        } else {
            bottomView.setVisibility(View.GONE);
        }
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
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.TAKE_PHOTO, TAG);
                })
                .onNegative((dialog, which) -> {
                    // 从相册获取图片
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.CHOOSE_PHOTO, TAG);
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
            bitmap = result.data;
            presenter.modifyAvatar(activity(), group, bitmap);
        } else {
            // 图片获取失败
            // no-op
        }
    }

    @Override
    public void setAutoAdd() {
        showSelectDialog(autoAddItem,
                "入群方式",
                R.drawable.ic_add_circle_24dp,
                text -> {
                    presenter.modifyAutoAdd(activity(), group, AUTO.equals(text));
                }, AUTO, NO_AUTO
        );
    }


    @Override
    public void setShowMemberName() {
        showSelectDialog(
                showMemberNameItem,
                "显示成员名",
                R.drawable.ic_star_half_24dp,
                text -> {
                    presenter.modifyShowMemberName(activity(), group, "是".equals(text));
                }, "是", "否"
        );
    }

    @Override
    public void setKeepSilent() {
        showSelectDialog(
                keepSilentItem,
                "消息免打扰",
                R.drawable.ic_keep_silent_24dp,
                text -> {
                    presenter.modifyKeepSilent(activity(), group, "是".equals(text));
                }, "是", "否"
        );

    }

    @Override
    public void setRejectMsg() {
        showSelectDialog(
                rejectMsgItem,
                "屏蔽群聊",
                R.drawable.ic_reject_msg_24dp,
                text -> {
                    presenter.modifyRejectMsg(activity(), group, "是".equals(text));
                }, "是", "否"
        );

    }

    /**
     * DESC: 显示选择弹框
     * Created by jinphy, on 2018/3/12, at 16:12
     */
    private void showSelectDialog(
            MenuItemView item,
            String title,
            int iconId,
            Consumer<String> onSelect,
            CharSequence... items) {
        if (activity() == null) {
            return;
        }
        new MaterialDialog.Builder(activity())
                .title(title)
                .titleColor(colorPrimary())
                .iconRes(iconId)
                .items(items)
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    if (!TextUtils.isEmpty(text)) {
                        item.content(text);
                        dialog.dismiss();
                        if (onSelect != null) {
                            onSelect.accept(text.toString());
                        }
                        return true;
                    }
                    App.showToast("请选择是否自动添加新成员！", false);
                    return false;
                })
                .autoDismiss(false)
                .cancelable(false)
                .widgetColor(colorPrimary())
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .onNegative((dialog, which) -> dialog.dismiss())
                .dismissListener(dialog -> MenuItemView.removeCurrent())
                .show();
    }

    @Override
    public void whenModifyAvatarOk() {
        avatarItem.iconDrawable(new BitmapDrawable(getResources(), bitmap));
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void whenModifyNameOk() {
        String name = nameItem.content().toString();
        group.setName(name);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void whenModifyMaxCountOk(int maxCount) {
        group.setMaxCount(maxCount);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());

    }

    @Override
    public void whenModifyAutoAddOk(boolean autoAdd) {
        group.setAutoAdd(autoAdd);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void whenModifyShowMemberNameOk(boolean showMemberName) {
        group.setShowMemberName(showMemberName);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());
        EventBus.getDefault().post(EBMessage.updateGroup());
    }

    @Override
    public void whenModifyKeepSilentOk(boolean keepSilent) {
        group.setKeepSilent(keepSilent);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void whenModifyRejectMsgOk(boolean rejectMsg) {
        group.setRejectMsg(rejectMsg);
        presenter.updateGroup(group);
        EventBus.getDefault().post(new EBUpdateView());
    }

    private boolean exit = false;

    @Override
    public void whenExitGroupOk() {
        App.showToast("退出群聊成功！", false);
        exit = true;
        finishActivity();
    }

    @Override
    public void whenBreakGroupOk() {
        App.showToast("解散群聊成功！", false);
        exit = true;
        finishActivity();
    }

    @Override
    public void whenDeleteGroupLocalOk() {
        App.showToast("群聊已删除！", false);
        exit = true;
        finishActivity();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        if ("FromModifyGroupFragment".equals(msg.data)) {
            return;
        }
        if (App.activity() instanceof ModifyGroupActivity){
            App.showToast("该群信息已更新！", false);
        }

        group = presenter.getGroup(groupNo);
        if (group == null) {
            if (App.activity() instanceof ModifyGroupActivity) {
                App.showToast("该群已经被删除！", false);
            }
            finishActivity();
        } else {
            setupViews();
        }
    }
}
