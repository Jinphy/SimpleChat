package com.example.jinphy.simplechat.modules.modify_friend_info;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.Friend_;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy, on 2018/3/2, at 13:55
 */
public class ModifyFriendInfoFragment extends BaseFragment<ModifyFriendInfoPresenter> implements ModifyFriendInfoContract.View {



    private String account;
    private Friend friend;
    private String date;
    private boolean agree = false;

    private MenuItemView avatarAndNameItem;
    private MenuItemView accountItem;
    private MenuItemView remarkItem;
    private MenuItemView statusItem;
    private MenuItemView signatureItem;
    private MenuItemView dateItem;
    private MenuItemView sexItem;
    private MenuItemView addressItem;

    private View btnAgree;
    private View btnRefuse;
    private View btnDelete;
    private View btnBlackList;
    private View btnSendMsg;


    public ModifyFriendInfoFragment() {
        // Required empty public constructor
    }

    public static ModifyFriendInfoFragment newInstance(String account) {
        ModifyFriendInfoFragment fragment = new ModifyFriendInfoFragment();
        Bundle args = new Bundle();
        args.putString(Friend_.account.name, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            this.presenter = getPresenter();
        }

        account = getArguments().getString(Friend_.account.name);
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_modify_friend_info;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        avatarAndNameItem = view.findViewById(R.id.item_avatar_name);
        accountItem = view.findViewById(R.id.item_account);
        remarkItem = view.findViewById(R.id.item_remark);
        statusItem = view.findViewById(R.id.item_status);
        signatureItem = view.findViewById(R.id.item_signature);
        dateItem = view.findViewById(R.id.item_date);
        sexItem = view.findViewById(R.id.item_sex);
        addressItem = view.findViewById(R.id.item_address);

        btnAgree = view.findViewById(R.id.btn_agree);
        btnRefuse = view.findViewById(R.id.btn_refuse);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnBlackList = view.findViewById(R.id.btn_black_list);
        btnSendMsg = view.findViewById(R.id.btn_send_msg);
    }

    @Override
    protected void setupViews() {
        friend = presenter.getFriend(account);
        Bitmap bitmap = ImageUtil.loadAvatar(friend.getAccount(), 500, 500);
        if (bitmap != null) {
            avatarAndNameItem.iconDrawable(new BitmapDrawable(getResources(), bitmap));
        }
        avatarAndNameItem.contentHint(friend.getName());
        accountItem.contentHint(friend.getAccount());
        remarkItem.content(friend.getRemark());
        switch (friend.getStatus()) {
            case Friend.status_ok:
                statusItem.contentHint("正常");
                btnDelete.setVisibility(View.VISIBLE);
                btnBlackList.setVisibility(View.VISIBLE);
                btnSendMsg.setVisibility(View.VISIBLE);
                break;
            case Friend.status_waiting:
                statusItem.contentHint("等待通过好友认证");
                btnAgree.setVisibility(View.VISIBLE);
                btnRefuse.setVisibility(View.VISIBLE);
                break;
            case Friend.status_black_listed:
                statusItem.contentHint("被对方拉入黑名单");
                btnDelete.setVisibility(View.VISIBLE);
                break;
            case Friend.status_black_listing:
                statusItem.contentHint("已拉入黑名单");
                btnDelete.setVisibility(View.VISIBLE);
                btnBlackList.setVisibility(View.VISIBLE);
                btnSendMsg.setVisibility(View.VISIBLE);
                TextView view = (TextView) btnBlackList.findViewById(R.id.black_list);
                view.setText("恢复拉黑");
                break;
            case Friend.status_refuse:
                statusItem.contentHint("您已拒绝对方好友申请");
                break;
            case Friend.status_deleted:
                statusItem.contentHint("已删除");
                btnDelete.setVisibility(View.VISIBLE);
                break;
            case Friend.status_readd:
                statusItem.contentHint("已重新添加，等待对方同意");
                btnDelete.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        signatureItem.contentHint(friend.getSignature());
        if (!TextUtils.isEmpty(friend.getDate())) {
            dateItem.contentHint(StringUtils.formatDate(Long.valueOf(friend.getDate())));
        }
        sexItem.contentHint(friend.getSex());
        addressItem.contentHint(friend.getAddress());
    }

    @Override
    protected void registerEvent() {
        btnAgree.findViewById(R.id.agree).setOnClickListener(v->{
            date = System.currentTimeMillis() + "";
            agree = true;
            Map<String, Object> params = newParams()
                    .add(Api.Key.receiveAccount, account)
                    .add(Api.Key.remark, remarkItem.content())
                    .add(Api.Key.date, date)
                    .add(Api.Key.confirm, 1)
                    .build();

            presenter.addFriend(activity(), params);
        });

        btnRefuse.findViewById(R.id.refuse).setOnClickListener(v -> {
            agree = false;
            Map<String, Object> params = newParams()
                    .add(Api.Key.receiveAccount, account)
                    .add(Api.Key.confirm, 0)
                    .build();
            presenter.addFriend(activity(), params);
        });

        btnDelete.findViewById(R.id.delete).setOnClickListener(v->{
            new MaterialDialog.Builder(App.activity())
                    .title("提示")
                    .titleColor(App.activity().colorPrimary())
                    .icon(ImageUtil.getDrawable(App.activity(),
                            R.drawable.ic_warning_24dp, App.activity().colorPrimary()))
                    .content("继续操作将会删除该好友，是否继续？")
                    .positiveText("继续")
                    .negativeText("不了")
                    .cancelable(false)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveColor(App.activity().colorPrimary())
                    .onPositive((dialog, which) -> {
                        if (Friend.status_deleted.equals(friend.getStatus())) {
                            this.afterDeleteFriend();
                            return;
                        }
                        Map<String, Object> params = newParams()
                                .add(Api.Key.account, friend.getAccount())
                                .add(Api.Key.owner, friend.getOwner())
                                .build();
                        presenter.deleteFriend(activity(), params);
                    })
                    .show();

        });

        btnBlackList.findViewById(R.id.black_list).setOnClickListener(v->{
            if (Friend.status_ok.equals(friend.getStatus())) {
                Map<String, Object> params = newParams()
                        .add(Api.Key.account, friend.getAccount())
                        .add(Api.Key.owner, friend.getOwner())
                        .add(Api.Key.status, Friend.status_black_listing)
                        .build();
                presenter.modifyStatus(activity(),params);
            } else if (Friend.status_black_listing.equals(friend.getStatus())) {
                Map<String, Object> params = newParams()
                        .add(Api.Key.account, friend.getAccount())
                        .add(Api.Key.owner, friend.getOwner())
                        .add(Api.Key.status, Friend.status_ok)
                        .build();
                presenter.modifyStatus(activity(), params);
            }
        });

        btnSendMsg.findViewById(R.id.send_msg).setOnClickListener(v->{
            ChatActivity.start(activity(), friend.getAccount());
        });
    }

    @Override
    public void afterAddFriend() {
        if (agree) {
            App.showToast("已同意好友申请！", false);
            friend.setDate(date);
            if (!TextUtils.isEmpty(remarkItem.content())) {
                friend.setRemark(remarkItem.content().toString());
            }
            friend.setStatus(Friend.status_ok);
            statusItem.contentHint("正常");
            dateItem.contentHint(StringUtils.formatDate(Long.valueOf(friend.getDate())));
            btnDelete.setVisibility(View.VISIBLE);
            btnBlackList.setVisibility(View.VISIBLE);
            btnSendMsg.setVisibility(View.VISIBLE);
        } else {
            App.showToast("已拒绝好友申请", false);
            friend.setStatus(Friend.status_refuse);
            statusItem.contentHint("您已拒绝对方好友申请");
        }
        presenter.saveFriend(friend);
        btnRefuse.setVisibility(View.GONE);
        btnAgree.setVisibility(View.GONE);
        // TODO: 2018/3/2 添加一条好友添加成功的信息 ：我们已经是好友了，想聊随时找我哈！
    }

    @Override
    protected void onKeyboardEvent(boolean open) {
        super.onKeyboardEvent(open);
        if (!open) {
            MenuItemView.removeCurrent();
        }
    }


    /**
     * DESC: 创建菜单
     * Created by jinphy, on 2018/1/8, at 20:52
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //        inflater.inflate(R.menu.menu_chat_fragment,menu);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        if (!TextUtils.isEmpty(remarkItem.content())) {
            if (StringUtils.notEqual(friend.getRemark(), remarkItem.content().toString())) {
                switch (friend.getStatus()) {
                    case Friend.status_ok:
                    case Friend.status_black_listed:
                    case Friend.status_black_listing:
                        new MaterialDialog.Builder(App.activity())
                                .title("提示")
                                .titleColor(App.activity().colorPrimary())
                                .icon(ImageUtil.getDrawable(App.activity(),
                                        R.drawable.ic_warning_24dp, App.activity().colorPrimary()))
                                .content("好友备注已修改，是否保存？")
                                .positiveText("保存")
                                .negativeText("不了")
                                .cancelable(false)
                                .contentGravity(GravityEnum.CENTER)
                                .positiveColor(App.activity().colorPrimary())
                                .onPositive((dialog, which) -> {
                                    Map<String, Object> params = newParams()
                                            .add(Api.Key.account, friend.getAccount())
                                            .add(Api.Key.owner, friend.getOwner())
                                            .add(Api.Key.remark, remarkItem.content())
                                            .build();
                                    presenter.modifyRemark(activity(), params);
                                })
                                .onNegative((dialog, which) -> finishActivity())
                                .show();
                        return false;
                    default:
                        break;

                }
            }
        }
        finishActivity();
        return false;
    }

    @Override
    public void afterModifyRemark() {
        App.showToast("备注修改成功！", false);
        friend.setRemark(remarkItem.content().toString());
        presenter.saveFriend(friend);
        finishActivity();
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void afterModifyStatus() {
        App.showToast("状态修改成功！", false);
        TextView view = btnBlackList.findViewById(R.id.black_list);
        if (Friend.status_ok.equals(friend.getStatus())) {
            friend.setStatus(Friend.status_black_listing);
            view.setText("恢复拉黑");
            statusItem.contentHint("已拉入黑名单");
        } else {
            friend.setStatus(Friend.status_ok);
            view.setText("拉黑");
            statusItem.contentHint("正常");
        }
        presenter.saveFriend(friend);
        EventBus.getDefault().post(new EBUpdateView());
    }

    @Override
    public void afterDeleteFriend() {
        App.showToast("好友已删除！", false);
        presenter.deleteFriendLocal(friend);
        finishActivity();
        EventBus.getDefault().post(new EBUpdateView());
    }
}
