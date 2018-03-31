package com.example.jinphy.simplechat.custom_view.dialog.friend_selector;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC: 选择好友对话框
 * Created by jinphy on 2018/3/17.
 */
public class FriendSelector extends AlertDialog implements FriendSelectorInterface<CheckedFriend>{

    private int width = 350;

    private int height = 500;

    private CharSequence title = "请选择";

    private int cancelColor = 0xff777777;

    private int titleColor = 0xff4527A0;

    private int confirmColor = 0xff558B2F;

    private ImageView btnCancel;

    private TextView btnConfirm;

    private RecyclerView recyclerView;

    private View emptyView;

    private TextView titleView;

    private MyAdapter<CheckedFriend> adapter;

    private FriendSelectorPresenter presenter;

    private List<String> excludeAccounts;


    private SChain.Consumer<List<CheckedFriend>> onSelect;

    public static FriendSelectorInterface<CheckedFriend> create(Context context) {
        return new FriendSelector(context);
    }


    protected FriendSelector(Context context) {
        super(context);
        setCancelable(false);
        presenter = new FriendSelectorPresenter(this);
        excludeAccounts = new LinkedList<>();

        // 设置View时不能再onCreate()中设置，否则无法显示
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config();

        registerEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_select_friend_dialog, null, false);
        setView(view);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        titleView = view.findViewById(R.id.title_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        titleView.setText(title);
        titleView.setTextColor(titleColor);
        btnCancel.setImageDrawable(
                ImageUtil.getDrawable(getContext(),R.drawable.ic_arrow_left_24dp, cancelColor));
        btnConfirm.setTextColor(confirmColor);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        initAdapter();

        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        adapter = MyAdapter.<CheckedFriend>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_select_item)
                .onCreateView(holder -> {
                    // 头像
                    holder.circleImageViews(R.id.avatar_view);
                    // checkbox
                    holder.checkBoxes(R.id.check_box);
                    // 昵称
                    holder.textViews(R.id.name_view);
                })
                .onBindView((holder, item, position) -> {
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.checkBox[0].setChecked(item.isChecked());

                    holder.setClickedViews(holder.item);
                    //                    holder.setLongClickedViews(holder.textView[0]);
                })
                .data(presenter.loadFriends(excludeAccounts))
                .onClick((v, item, holder, type, position) -> {
                    item.setChecked(!item.isChecked());
                    holder.checkBox[0].setChecked(item.isChecked());
                })
                .into(recyclerView);
    }

    private void registerEvent() {
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            if (onSelect != null) {
                List<CheckedFriend> data = adapter.getData(CheckedFriend::isChecked);
                if (data.size() == 0) {
                    App.showToast("您还未选择任何一项", false);
                    return;
                }
                onSelect.accept(data);
            }
            dismiss();
        });


    }

    private void config() {
        //        getWindow().setLayout(600,300);
        Window window = getWindow();
        final WindowManager.LayoutParams attrs = window.getAttributes();
        // 设置该值可以自己设置的布局背景的形状才能够显示，比如cardView的圆角
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

        if (width > 0) {
            attrs.width = ScreenUtils.dp2px(getContext(), width);
        }
        if (height > 0) {
            attrs.height = ScreenUtils.dp2px(getContext(), height);
        }

        window.setAttributes(attrs);

    }


    @Override
    public FriendSelectorInterface<CheckedFriend> width(int dbValue) {
        this.width = dbValue;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend> height(int dbValue) {
        this.height =  dbValue;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend> cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend> onSelect(SChain.Consumer<List<CheckedFriend>> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend>  title(CharSequence title) {
        this.title = title;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend>  cancelColor(int color) {
        this.cancelColor = color;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend>  titleColor(int color) {
        this.titleColor = color;
        return this;
    }

    @Override
    public FriendSelectorInterface<CheckedFriend>  confirmColor(int color) {
        this.confirmColor = color;
        return this;
    }

    /**
     * DESC: 设置不包含的好友账号
     * Created by jinphy, on 2018/3/18, at 8:57
     */
    @Override
    public FriendSelectorInterface<CheckedFriend> exclude(List<String> exclude) {
        LogUtils.e(exclude);
        if (exclude == null || exclude.size() == 0) {
            return this;
        }
        this.excludeAccounts.addAll(exclude);
        adapter.update(presenter.loadFriends(excludeAccounts));
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    @Override
    public FriendSelector display() {
        show();
        return this;
    }
}
