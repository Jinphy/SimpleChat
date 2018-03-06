package com.example.jinphy.simplechat.modules.group.create_group;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.event_bus.EBBitmap;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.modules.chat.ChatActivity;
import com.example.jinphy.simplechat.modules.pick_photo.PickPhotoActivity;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private Bitmap avatarBitmap;


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

    }

    @Override
    protected void findViewsById(View view) {
        avatarItem = view.findViewById(R.id.item_avatar);
        nameItem = view.findViewById(R.id.item_name);
        autoAddItem = view.findViewById(R.id.item_auto_add);
        maxCountItem = view.findViewById(R.id.item_max_count);
        btnCreate = view.findViewById(R.id.btn_create);
    }

    @Override
    protected void setupViews() {
        maxCountItem.contentHint("请输入1~" + Group.DEFAULT_MAX_COUNT + "的整数！");
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

            Map<String, Object> params = newParams().add(Api.Key.avatar, StringUtils
                    .bitmapToBase64(avatarBitmap))
                    .add(Api.Key.name, name)
                    .add(Api.Key.autoAdd, autoAdd)
                    .add(Api.Key.maxCount, maxCount)
                    .add(Api.Key.accessToken, presenter.getAccessToken())
                    .build();
            presenter.createGroup(activity(), params);
        });
    }


    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {
            MenuItemView.removeCurrent();
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
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.TAKE_PHOTO);
                })
                .onNegative((dialog, which) -> {
                    // 从相册获取图片
                    PickPhotoActivity.start(activity(), PickPhotoActivity.Option.CHOOSE_PHOTO);
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
                .iconRes(R.drawable.ic_flower_24dp)
                .items(getString(R.string.yes), getString(R.string.no))
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    autoAddItem.content(text);
                    return true;
                })
                .widgetColor(colorPrimary())
                .positiveText("确定")
                .negativeText("取消")
                .cancelable(false)
                .positiveColor(colorPrimary())
                .negativeColorRes(R.color.color_red_D50000)
                .dismissListener(dialog -> MenuItemView.removeCurrent())
                .show();
    }

    /**
     * DESC: 获取图片的结果
     * Created by jinphy, on 2018/1/10, at 21:11
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pickPhotoResult(EBBitmap result) {
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
    }
}
