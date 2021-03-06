package com.example.jinphy.simplechat.modules.group.create_group;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.event_bus.EBUpdateView;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.pick_photo.PickPhotoActivity;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by Jinphy, on 2018/3/6, at 9:26
 */
public class CreateGroupFragment extends BaseFragment<CreateGroupPresenter> implements CreateGroupContract.View {

    private MenuItemView avatarItem;
    private MenuItemView nameItem;
    private MenuItemView autoAddItem;
    private MenuItemView maxCountItem;
    private View btnCreate;
    private RecyclerView recyclerView;

    private Bitmap avatarBitmap;
    private LinearLayoutManager linearLayoutManager;
    private List<String> members;

    public static final String TAG = "CreateGroupFragment";
    private MyAdapter<CheckedFriend> adapter;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * DESC:
     * Created by Jinphy, on 2018/3/6, at 9:27
     */
    public static CreateGroupFragment newInstance() {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        return R.layout.fragment_create_group;
    }

    @Override
    protected void initData() {
        members = new LinkedList<>();
    }

    @Override
    protected void findViewsById(View view) {
        avatarItem = view.findViewById(R.id.item_avatar);
        nameItem = view.findViewById(R.id.item_name);
        autoAddItem = view.findViewById(R.id.item_auto_add);
        maxCountItem = view.findViewById(R.id.item_max_count);
        btnCreate = view.findViewById(R.id.btn_create);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void setupViews() {
        maxCountItem.contentHint("请输入1~" + Group.DEFAULT_MAX_COUNT + "的整数！");

        linearLayoutManager = new LinearLayoutManager(
                getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = MyAdapter.<CheckedFriend>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_group_member_item)
                .data(presenter.loadFriends())
                .onCreateView(holder -> {
                    // avatar
                    holder.circleImageViews(R.id.avatar_view);
                    // name
                    holder.textViews(R.id.name_view);
                    // checkView、clickView
                    holder.views(R.id.check_view, R.id.click_view);
                })
                .onBindView((holder, item, position) -> {
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    } else {
                        holder.circleImageView[0].setImageResource(R.drawable.ic_person_48dp);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.view[0].setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
                    holder.setClickedViews(holder.view[1]);
                })
                .onClick((v, item, holder, type, position) -> {
                    item.setChecked(!item.isChecked());
                    holder.view[0].setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
                })
                .into(recyclerView);

    }

    @Override
    protected void registerEvent() {
        avatarItem.onClick((menuItemView, hasFocus) -> {
            pickPhoto();
        });
        autoAddItem.onClick((menuItemView, hasFocus) -> {
            setAutoAdd();
        });
        btnCreate.setOnClickListener(view -> {
            String countStr = maxCountItem.content().toString();
            int maxCount;
            if (StringUtils.isNumber(countStr)) {
                maxCount = Integer.valueOf(countStr);
                if (maxCount <1 || maxCount > Group.DEFAULT_MAX_COUNT) {
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
            members.clear();
            adapter.forEach(item -> {
                if (item.isChecked()) {
                    members.add(item.getAccount());
                }
            });
            String currentAccount = presenter.getCurrentAccount();
            members.add(currentAccount);

            Map<String, Object> params = newParams()
                    .add(Api.Key.avatar, StringUtils.bitmapToBase64(avatarBitmap))
                    .add(Api.Key.name, name)
                    .add(Api.Key.autoAdd, autoAdd)
                    .add(Api.Key.maxCount, maxCount)
                    .add(Api.Key.accessToken, presenter.getAccessToken())
                    .add(Api.Key.members, GsonUtils.toJson(members))
                    .add(Api.Key.creator, currentAccount)
                    .build();
            presenter.createGroup(activity(), params);
        });
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
        new MaterialDialog.Builder(activity())
                .title("提示")
                .titleColor(colorPrimary())
                .iconRes(R.drawable.ic_warning_24dp)
                .content("确定放弃创建群聊，直接退出吗？")
                .contentGravity(GravityEnum.CENTER)
                .widgetColor(colorPrimary())
                .positiveText("确定")
                .negativeText("取消")
                .cancelable(false)
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .onPositive((dialog,which)-> finishActivity())
                .show();
        return false;
    }


    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {
            MenuItemView.removeCurrent();
        }
        recyclerView.setVisibility(open?View.GONE:View.VISIBLE);

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


    @Override
    public void setAutoAdd() {
        if (activity() == null) {
            return;
        }
        new MaterialDialog.Builder(activity())
                .title("选择成员添加是否验证")
                .titleColor(colorPrimary())
                .iconRes(R.drawable.ic_add_circle_24dp)
                .items(getString(R.string.yes), getString(R.string.no))
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    if (!TextUtils.isEmpty(text)) {
                        autoAddItem.content(text);
                        dialog.dismiss();
                        return true;
                    }
                    App.showToast("请选择是否自动添加新成员！", false);
                    return false;
                })
                .autoDismiss(false)
                .widgetColor(colorPrimary())
                .positiveText("确定")
                .negativeText("取消")
                .cancelable(false)
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .onNegative((dialog, which) -> dialog.dismiss())
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
            avatarItem.iconDrawable(new BitmapDrawable(getResources(), avatarBitmap));
        } else {
            // 图片获取失败
            // no-op
        }
    }

    @Override
    public void whenCreateGroupOk(Group group) {
        App.showToast("群聊创建成功！", false);
        ChatActivity.start(activity(), group.getGroupNo());
        finishActivity();
        presenter.saveMembers(group, members);
        ImageUtil.storeAvatar(group.getGroupNo(), avatarBitmap);
        EventBus.getDefault().post(new EBUpdateView());
    }
}
