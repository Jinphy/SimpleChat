package com.example.jinphy.simplechat.modules.add_friend;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.custom_view.MenuItemView;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.Map;

/**
 * DESC:
 * Created by jinphy, on 2018/1/15, at 14:08
 */
public class AddFriendFragment extends BaseFragment<AddFriendPresenter> implements AddFriendContract.View {

    public static final String USER_JSON = "USER_JSON";

    private Friend friend;

    private MenuItemView friendItem;
    private MenuItemView remarkItem;
    private MenuItemView verifyMsgItem;
    private View addFriendBtn;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    public static AddFriendFragment newInstance(String userJson) {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putString(USER_JSON, userJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            friend = GsonUtils.toBean(arguments.getString(USER_JSON), Friend.class);
        }
        if (presenter == null) {
            this.presenter = getPresenter();
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_add_friend;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findViewsById(View view) {
        friendItem = view.findViewById(R.id.item_friend);
        remarkItem = view.findViewById(R.id.item_remark);
        verifyMsgItem = view.findViewById(R.id.item_verify_msg);
        addFriendBtn = view.findViewById(R.id.btn_add_friend);
    }

    @Override
    protected void setupViews() {
        Bitmap bitmap = StringUtils.base64ToBitmap(friend.getAvatar());
        if (bitmap != null) {
            friendItem.iconDrawable(new BitmapDrawable(getResources(), bitmap));
        }
        friendItem.content(friend.getName());

    }

    @Override
    protected void registerEvent() {

        /**
         * DESC: 添加好友按钮的点击事件
         * Created by jinphy, on 2018/2/27, at 8:45
         */
        addFriendBtn.setOnClickListener(view->{
            Map<String, Object> params = newParams()
                    .add(Api.Key.requestAccount, presenter.getCurrentAccount())
                    .add(Api.Key.receiveAccount, friend.getAccount())
                    .add(Api.Key.remark, remarkItem.content())
                    .add(Api.Key.verifyMsg, verifyMsgItem.content())
                    .build();
            presenter.addFriend(activity(), params);

        });
    }

    @Override
    public void finish() {
        friend.setStatus(Friend.status_waiting);
        if (!TextUtils.isEmpty(remarkItem.content())) {
            friend.setRemark(remarkItem.content().toString());
        }
        presenter.saveFriend(friend);
        activity().finish();
    }

    @Override
    protected void onKeyboardEvent(boolean open) {
        if (!open) {
            MenuItemView.removeCurrent();
        }
    }
}
