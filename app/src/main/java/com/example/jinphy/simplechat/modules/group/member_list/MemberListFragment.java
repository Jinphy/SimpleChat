package com.example.jinphy.simplechat.modules.group.member_list;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.custom_view.dialog.friend_selector.FriendSelector;
import com.example.jinphy.simplechat.custom_view.dialog.my_dialog.MyDialog;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.modules.pick_photo.PickPhotoActivity;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy, on 2018/3/14, at 15:31
 */
public class MemberListFragment extends BaseFragment<MemberListPresenter> implements
        MemberListContract.View {

    public static final String GROUP_NO = "GROUP_NO";

    public static final String TAG = "MemberListFragment";

    private RecyclerView recyclerView;
    private View bottomView;
    private View btnRemoveMembers;
    private View btnBuildNewGroup;
    private TextView btnSelectAll;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter<CheckableMember> adapter;

    private String groupNo;

    /**
     * DESC: 标志是否显示CheckBox
     * Created by jinphy, on 2018/3/18, at 14:07
     */
    private boolean showCheckbox;

    /**
     * DESC: 标志是否全选
     * Created by jinphy, on 2018/3/18, at 14:07
     */
    private boolean selectAll;

    private Bitmap bitmapForBuildGroup;

    private MenuItemView avatarView;

    private List<String> checkedAccounts;

    public MemberListFragment() {
        // Required empty public constructor
    }

    /**
     * DESC:
     * Created by jinphy, on 2018/3/14, at 15:31
     */
    public static MemberListFragment newInstance(String groupNo) {
        MemberListFragment fragment = new MemberListFragment();
        fragment.groupNo = groupNo;
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
        return R.layout.fragment_member_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        bottomView = view.findViewById(R.id.bottom_view);
        btnRemoveMembers = view.findViewById(R.id.btn_remove_members);
        btnBuildNewGroup = view.findViewById(R.id.btn_build_new_group);
        btnSelectAll = view.findViewById(R.id.btn_select_all_view);
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter = MyAdapter.<CheckableMember>newInstance()
                .data(presenter.loadMembers(groupNo))
                .onInflate(viewType -> R.layout.layout_member_list_item)
                .onCreateView(holder -> {
                    holder.circleImageView[0] = holder.item.findViewById(R.id.avatar_view);
                    holder.textView[0] = holder.item.findViewById(R.id.name_view);
                    holder.textView[1] = holder.item.findViewById(R.id.account_view);
                    holder.view[0] = holder.item.findViewById(R.id.more_view);
                    holder.view[1] = holder.item.findViewById(R.id.not_allow_chat_view);
                    holder.checkBox[0] = holder.item.findViewById(R.id.check_box);
                })
                .onBindView((holder, item, position) -> {
                    // 设置头像
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    } else {
                        holder.circleImageView[0].setImageResource(R.drawable.ic_person_48dp);
                    }
                    // 设置名字
                    holder.textView[0].setText(item.getName());

                    // 设置是否显示允许聊天的提示
                    if (item.isAllowChat()) {
                        holder.view[1].setVisibility(View.GONE);
                    } else {
                        holder.view[1].setVisibility(View.VISIBLE);
                    }

                    // 设置账号
                    holder.textView[1].setText(item.getAccount());

                    // 设置CheckBox
                    if (showCheckbox) {
                        holder.view[0].setVisibility(View.GONE);
                        holder.checkBox[0].setVisibility(View.VISIBLE);
                        holder.checkBox[0].setChecked(item.isChecked());
                    } else {
                        holder.view[0].setVisibility(View.VISIBLE);
                        holder.checkBox[0].setVisibility(View.GONE);
                    }

                    // 设置需要注册事件的view
                    holder.setClickedViews(holder.item, holder.view[0]);
                    holder.setLongClickedViews(holder.item);
                })
                .onClick((v, item, holder, type, position) -> {
                    switch (v.getId()) {
                        case R.id.more_view:
                            showItemMoreDialog(item, position);
                            break;
                        default:
                            if (showCheckbox) {
                                item.setChecked(!item.isChecked());
                                holder.checkBox[0].setChecked(item.isChecked());
                            } else {
                                if (StringUtils.equal(item.getAccount(), item.getOwner())) {
                                    ModifyUserActivity.start(activity());
                                } else {
                                    ModifyFriendInfoActivity.start(activity(), item.getAccount());
                                }
                            }
                            break;
                    }
                })
                .onLongClick((v, item, holder, type, position) -> {
                    setShowCheckBoxStatus();
                    if (showCheckbox) {
                        holder.checkBox[0].setChecked(true);
                    }
                    return true;
                })
                .into(recyclerView);


        View removeView = (View) btnRemoveMembers.getParent();
        if (presenter.isCreator(groupNo)) {
            // 是群主
            removeView.setVisibility(View.VISIBLE);
        } else {
            removeView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void registerEvent() {

        btnRemoveMembers.setOnClickListener(v -> {
            List<String> checkedMembers = getCheckedAccounts();
            for (String member : checkedMembers) {
                if (StringUtils.equal(member, presenter.getOwner())) {
                    App.showToast("删除成员不能包括自己", false);
                    return;
                }
            }
            if (checkedMembers.size() == 0) {
                App.showToast("请选择需要移出的成员", false);
                return;
            }
            new MaterialDialog.Builder(activity())
                    .title("移除成员")
                    .titleColor(colorPrimary())
                    .content("您将移出所选成员，并且操作不可恢复，是否继续？")
                    .contentGravity(GravityEnum.CENTER)
                    .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                    .positiveText("确定")
                    .negativeText("不了")
                    .positiveColor(colorPrimary())
                    .negativeColorRes(R.color.color_red_D50000)
                    .onPositive((dialog, which) -> {
                        presenter.removeMembers(activity(), groupNo, checkedMembers);
                        setShowCheckBoxStatus();
                    })
                    .show();


        });

        btnBuildNewGroup.setOnClickListener(v -> {
            checkedAccounts = getCheckedAccounts();
            if (checkedAccounts.size() == 0) {
                App.showToast("您还未选择任何一项！", false);
                return;
            }
            String owner = presenter.getOwner();
            boolean hasCheckedSelf = false;
            for (String account : checkedAccounts) {
                if (StringUtils.equal(account, owner)) {
                    hasCheckedSelf = true;
                    break;
                }
            }
            if (!hasCheckedSelf) {
                App.showToast("创建新群聊必须选中自己！", false);
                return;
            }

            showBuildNewGroupDialog();
        });

        btnSelectAll.setOnClickListener(v -> {
            if (setSelectAll()) {
                btnSelectAll.setText("取消全选");
            } else {
                btnSelectAll.setText("全选");
            }
        });
    }

    /**
     * DESC: 获取所有选中的账号
     * Created by jinphy, on 2018/3/18, at 14:32
     */
    private List<String> getCheckedAccounts() {
        List<String> result = new LinkedList<>();
        adapter.forEach(item->{
            if (item.isChecked()) {
                result.add(item.getAccount());
            }
        });
        return result;
    }

    private List<String> getAllAccounts() {
        List<String> result = new LinkedList<>();
        adapter.forEach(item-> result.add(item.getAccount()));
        return result;
    }


    /**
     * DESC: 设置是否全选
     * Created by jinphy, on 2018/3/18, at 14:32
     */
    private boolean setSelectAll() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        selectAll = !selectAll;
        checkAll(selectAll);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(i);
        return selectAll;
    }

    /**
     * DESC: 设置是否显示CheckBox
     * Created by jinphy, on 2018/3/15, at 20:02
     */
    private void setShowCheckBoxStatus() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        showCheckbox = !showCheckbox;
        if (showCheckbox) {
            // 当显示CheckBox时需要重置所有的选中状态为false
            checkAll(false);
        }
        adapter.notifyDataSetChanged();
        if (showCheckbox) {
            bottomView.setVisibility(View.VISIBLE);
        } else {
            bottomView.setVisibility(View.GONE);
        }
        recyclerView.scrollToPosition(i);
    }

    /**
     * DESC: 设置所有项
     * Created by jinphy, on 2018/3/18, at 14:03
     */
    private void checkAll(boolean isChecked) {
        adapter.forEach(item -> item.setChecked(isChecked));
    }


    /**
     * DESC: 创建列表项菜单按钮弹框
     * Created by jinphy, on 2018/3/15, at 19:56
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showItemMoreDialog(CheckableMember member, int position) {
        MyDialog.Holder holder = MyDialog.create(activity())
                .view(R.layout.dialog_member_item)
                .width(200)
                .display();
        TextView allowToChatView = holder.view.findViewById(R.id.allow_to_chat_view);
        View modifyRemarkView = holder.view.findViewById(R.id.modify_remark_view);
        View removeMemberView = holder.view.findViewById(R.id.remove_member_view);
        if (member.isAllowChat()) {
            allowToChatView.setText("禁止发言");
        } else {
            allowToChatView.setText("允许发言");
        }
        if (StringUtils.equal(member.getAccount(), member.getOwner())) {
            holder.dialog.dismiss();
            App.showToast("该成员是本人，无需操作！", false);
            return;
        } else if (!presenter.isCreator(groupNo)) {
            removeMemberView.setVisibility(View.GONE);
            allowToChatView.setVisibility(View.GONE);
        }

        if (removeMemberView.getVisibility() == View.VISIBLE) {
            removeMemberView.setOnClickListener(v -> {
                holder.dialog.dismiss();
                new MaterialDialog.Builder(activity())
                        .title("移除成员")
                        .titleColor(colorPrimary())
                        .content("您将移出该成员，并且操作不可恢复，是否继续？")
                        .contentGravity(GravityEnum.CENTER)
                        .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                        .positiveText("确定")
                        .negativeText("不了")
                        .positiveColor(colorPrimary())
                        .negativeColorRes(R.color.color_red_D50000)
                        .onPositive((dialog, which) -> {
                            presenter.removeMember(activity(), member);
                        })
                        .show();

            });
        }
        if (allowToChatView.getVisibility() == View.VISIBLE) {
            allowToChatView.setOnClickListener(v -> {
                holder.dialog.dismiss();
                presenter.modifyAllowChat(activity(), member);
            });
        }
        if (modifyRemarkView.getVisibility() == View.VISIBLE) {
            modifyRemarkView.setOnClickListener(v -> {
                holder.dialog.dismiss();
                new MaterialDialog.Builder(activity())
                        .title("成员信息")
                        .titleColor(colorPrimary())
                        .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_person_48dp,
                                colorPrimary()))
                        .content("修改备注")
                        .autoDismiss(false)
                        .cancelable(false)
                        .widgetColor(colorPrimary())
                        .negativeText("取消")
                        .input("请输入备注信息", "", false,
                                (dialog, input) -> {
                                    if (ObjectHelper.isTrimEmpty(input.toString().trim())) {
                                        App.showToast("备注信息不能为空格等！", false);
                                    }
                                    dialog.dismiss();
                                    String remark = input.toString();
                                    presenter.modifyRemark(activity(), member, remark);
                                }
                        )
                        .positiveColor(colorPrimary())
                        .negativeColorRes(R.color.color_red_D50000)
                        .onNegative((dialog, which) -> dialog.dismiss())
                        .show();

            });
        }
    }

    /**
     * DESC: 创建右上角菜单弹框
     * Created by jinphy, on 2018/3/15, at 19:56
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showMoreDialog() {
        MyDialog.Holder holder = MyDialog.create(activity())
                .view(R.layout.dialog_member_more)
                .width(200)
                .display();
        Group group = presenter.getGroup(groupNo);
        View addMemberView = holder.view.findViewById(R.id.add_member_view);
        if (group.isMyGroup()) {
            addMemberView.setVisibility(View.VISIBLE);
            addMemberView.setOnClickListener(v -> {
                holder.dialog.dismiss();
                showSelectFriendsDialog();
            });
        } else {
            addMemberView.setVisibility(View.GONE);
        }


        holder.view.findViewById(R.id.select_view)
                .setOnClickListener(v -> {
                    holder.dialog.dismiss();
                    if (!showCheckbox) {
                        setShowCheckBoxStatus();
                    }
                });
    }

    /**
     * DESC: 显示选择好友的对话框
     * Created by jinphy, on 2018/3/18, at 13:58
     */
    private void showSelectFriendsDialog() {
        FriendSelector SelectorDialog = FriendSelector.create(activity())
                .titleColor(colorPrimary())
                .exclude(getAllAccounts())
                .onSelect(value -> {
                    List<String> selectedAccounts = new ArrayList<>(value.size());
                    for (CheckedFriend it : value) {
                        selectedAccounts.add(it.getAccount());
                    }
                    new MaterialDialog.Builder(activity())
                            .title("添加成员")
                            .titleColor(colorPrimary())
                            .content("所选的好友将会加入到该群聊，是否继续？")
                            .contentGravity(GravityEnum.CENTER)
                            .icon(ImageUtil.getDrawable(activity(), R.drawable.ic_warning_24dp, colorPrimary()))
                            .positiveText("确定")
                            .negativeText("不了")
                            .cancelable(false)
                            .positiveColor(colorPrimary())
                            .negativeColorRes(R.color.color_red_D50000)
                            .onPositive((dialog, which) -> {
                                dialog.dismiss();
                                presenter.addMembers(activity(), groupNo, selectedAccounts);
                            })
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();

                })
                .display();

    }

    private void showBuildNewGroupDialog() {
        MyDialog.Holder holder = MyDialog.create(activity())
                .view(R.layout.create_new_group_dialog)
                .cancelable(false)
                .width(350)
                .display();
        MenuItemView avatarItem = holder.view.findViewById(R.id.item_avatar);
        MenuItemView nameItem = holder.view.findViewById(R.id.item_name);
        MenuItemView autoAddItem = holder.view.findViewById(R.id.item_auto_add);
        MenuItemView maxCountItem = holder.view.findViewById(R.id.item_max_count);
        View cancelBtn = holder.view.findViewById(R.id.btn_cancel);
        View confirmBtn = holder.view.findViewById(R.id.btn_confirm);

        avatarView = avatarItem;

        avatarItem.onClick((menuItemView, hasFocus) -> {
            pickPhoto();
        });
        autoAddItem.onClick((menuItemView, hasFocus) -> {
            if ("自动添加".equals(menuItemView.content().toString())) {
                menuItemView.content("群主验证");
            } else {
                menuItemView.content("自动添加");
            }
        });

        cancelBtn.setOnClickListener(v -> {
            holder.dialog.dismiss();
            avatarView = null;
        });

        confirmBtn.setOnClickListener(v -> {
            String countStr = maxCountItem.content().toString();
            int maxCount;
            if (StringUtils.isNumber(countStr)) {
                maxCount = Integer.valueOf(countStr);
                if (maxCount < 1 || maxCount > Group.DEFAULT_MAX_COUNT) {
                    String s = "1~" + Group.DEFAULT_MAX_COUNT;
                    App.showToast(SChain.with("请输入" + s + "的整数！")
                            .colorForText(Color.GREEN, s)
                            .make(), false);
                    return;
                }
            } else {
                App.showToast(SChain.with("最大成员数必须是整数!").
                        colorForText(Color.RED, "整数").make(), false);
                maxCountItem.content("");
                return;
            }

            String name = nameItem.content().toString();
            if (TextUtils.isEmpty(name)) {
                name = "群聊";
            }
            boolean autoAdd = true;
            if (StringUtils.equal("否", autoAddItem.content().toString())) {
                autoAdd = false;
            }
            String creator = presenter.getOwner();

            Map<String, Object> params = newParams()
                    .add(Api.Key.avatar, StringUtils.bitmapToBase64(bitmapForBuildGroup))
                    .add(Api.Key.name, name)
                    .add(Api.Key.autoAdd, autoAdd)
                    .add(Api.Key.maxCount, maxCount)
                    .add(Api.Key.accessToken, presenter.getAccessToken())
                    .add(Api.Key.members, GsonUtils.toJson(checkedAccounts))
                    .add(Api.Key.creator, creator)
                    .build();
            presenter.createGroup(activity(), params);
            holder.dialog.dismiss();
        });
    }


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
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.CHOOSE_PHOTO,TAG);
                })
                .dismissListener(dialog -> MenuItemView.removeCurrent())
                .show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pickPhotoResult(EBBitmap result) {
        if (!TAG.equals(result.tag)) {
            return;
        }
        if (result.ok) {
            // 图片获取成功
            bitmapForBuildGroup = result.data;
            if (avatarView != null) {
                avatarView.iconDrawable(new BitmapDrawable(getResources(), bitmapForBuildGroup));
            }
        } else {
            // 图片获取失败
            // no-op
        }
    }



    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                showMoreDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onBackPressed() {
        if (showCheckbox) {
            setShowCheckBoxStatus();
        } else {
            finishActivity();
        }
        return false;
    }

    private void refresh() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(i);
    }

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {
            MenuItemView.removeCurrent();
        }
    }

    //-----------mvp 方法-------------------------------------------------
    @Override
    public void whenModifyRemarkOk() {
        App.showToast("备注修改成功！", false);
        refresh();
    }

    @Override
    public void whenModifyAllowChatOk(String msg) {
        App.showToast(msg, false);
        refresh();
    }

    @Override
    public void whenRemoveMemberOk(CheckableMember member) {
        App.showToast("成员：" + member.getName() + "(" + member.getAccount() + ")已被移除！", false);
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        adapter.remove(member);
        if (i < adapter.getItemCount()) {
            recyclerView.scrollToPosition(i);
        }
    }

    @Override
    public void whenRemoveMembersOk() {
        App.showToast("所选成员以删除！", false);
        adapter.remove(CheckableMember::isChecked);
    }


    @Override
    public void whenCreateGroupOk(Group group) {
        ChatActivity.start(activity(), group.getGroupNo());
        presenter.saveMembers(group, checkedAccounts);
        ImageUtil.storeAvatar(group.getGroupNo(), bitmapForBuildGroup);
        EventBus.getDefault().post(new EBUpdateView());
        App.showToast("群聊创建成功！", false);
    }


    //-----------------eventBus 方法 ----------------------------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        if (TAG.equals(msg.data)) {
            return;
        }
        adapter.update(presenter.loadMembers(groupNo));
        App.showToast("成员信息已更新", false);
    }


}
