package com.example.jinphy.simplechat.modules.group.member_list;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.custom_view.dialog.MyDialog;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.member.CheckableMember;
import com.example.jinphy.simplechat.modules.modify_friend_info.ModifyFriendInfoActivity;
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * DESC:
 * Created by jinphy, on 2018/3/14, at 15:31
 */
public class MemberListFragment extends BaseFragment<MemberListPresenter> implements
        MemberListContract.View {

    public static final String GROUP_NO = "GROUP_NO";

    private RecyclerView recyclerView;
    private View bottomView;
    private View btnRemoveMembers;
    private View btnBuildNewGroup;
    private TextView btnSelectAll;
    private LinearLayoutManager linearLayoutManager;
    private MemberListAdapter adapter;

    private String groupNo;

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
        adapter = new MemberListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.update(presenter.loadMembers(groupNo));

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
        adapter.onClick((view, item, type, position) -> {
            CheckableMember member = (CheckableMember) item;
            switch (view.getId()) {
                case R.id.more_view:
                    showItemMoreDialog(member, position);
                    break;
                default:
                    if (adapter.isShowCheckbox()) {
                        adapter.check(view);
                    } else {
                        if (StringUtils.equal(member.getAccount(), member.getOwner())) {
                            ModifyUserActivity.start(activity());
                        } else {
                            ModifyFriendInfoActivity.start(activity(), member.getAccount());
                        }
                    }
                    break;
            }
        });
        adapter.onLongClick((view, item, type, position) -> {
            setShowCheckBoxStatus();
            return true;
        });

        btnRemoveMembers.setOnClickListener(v -> {
            // TODO: 2018/3/15 要判断群主是否被选中，如果群主被选中，则应该提示用户，并终止操作
        });

        btnBuildNewGroup.setOnClickListener(v -> {

        });

        btnSelectAll.setOnClickListener(v -> {
            int i = linearLayoutManager.findFirstVisibleItemPosition();
            if (adapter.setSelectAll()) {
                btnSelectAll.setText("取消全选");
            } else {
                btnSelectAll.setText("全选");
            }
            recyclerView.scrollToPosition(i);
        });
    }

    /**
     * DESC: 设置是否显示CheckBox
     * Created by jinphy, on 2018/3/15, at 20:02
     */
    private void setShowCheckBoxStatus() {
        int i = linearLayoutManager.findFirstVisibleItemPosition();
        if (adapter.showCheckBox()) {
            bottomView.setVisibility(View.VISIBLE);
        } else {
            bottomView.setVisibility(View.GONE);
        }
        recyclerView.scrollToPosition(i);
    }


    /**
     * DESC: 创建列表项菜单按钮弹框
     * Created by jinphy, on 2018/3/15, at 19:56
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showItemMoreDialog(CheckableMember member, int position) {
        MyDialog.Holder holder = MyDialog.create(activity())
                .view(R.layout.menu_member_item)
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
                // TODO: 2018/3/15
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
                .view(R.layout.menu_member_more)
                .width(200)
                .display();
        holder.view.findViewById(R.id.add_member_view)
                .setOnClickListener(v -> {
                    holder.dialog.dismiss();
                    // TODO: 2018/3/15
                });

        holder.view.findViewById(R.id.select_view)
                .setOnClickListener(v -> {
                    holder.dialog.dismiss();
                    if (!adapter.isShowCheckbox()) {
                        setShowCheckBoxStatus();
                    }
                });
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
        if (adapter.isShowCheckbox()) {
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
    public void whenModifyRemarkOk() {
        App.showToast("备注修改成功！", false);
        refresh();
    }

    @Override
    public void whenModifyAllowChatOk(String msg) {
        App.showToast(msg, false);
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(EBUpdateView msg) {
        adapter.update(presenter.loadMembers(groupNo));
        App.showToast("成员信息已更新", false);
    }
}
