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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.custom_libs.my_adapter.MyAdapter;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
import com.example.jinphy.simplechat.utils.ImageUtil;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public class FriendSelector extends AlertDialog implements FriendSelectorInterface<CheckedFriend>{

    private int width = 200;

    private int height = 500;

    private CharSequence title = "请选择";

    private int cancelColor = 0xff777777;

    private int titleColor = 0xff4527A0;

    private int confirmColor = 0xff558B2F;

    private View btnCancel;

    private View btnConfirm;

    private RecyclerView recyclerView;

    private TextView titleView;

    private ImageView cancelView;

    private TextView confirmView;

    private MyAdapter<CheckedFriend> adapter;

    private FriendSelectorPresenter presenter;

    private List<String> exculdeAccount;


    private SChain.Consumer<List<CheckedFriend>> onSelect;

    public static FriendSelectorInterface<CheckedFriend> create(Context context) {
        return new FriendSelector(context);
    }


    protected FriendSelector(Context context) {
        super(context);
        setCancelable(false);
        presenter = new FriendSelectorPresenter(this);
        exculdeAccount = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        config();

        registerEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.menu_select_friend_dialog, null, false);
        setView(view);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        titleView = view.findViewById(R.id.title_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        cancelView = view.findViewById(R.id.cancel_view);
        confirmView = view.findViewById(R.id.confirm_view);

        titleView.setText(title);
        titleView.setTextColor(titleColor);
        cancelView.setImageDrawable(
                ImageUtil.getDrawable(getContext(),R.drawable.ic_arrow_left_24dp, cancelColor));
        confirmView.setTextColor(confirmColor);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(getAdapter());
    }

    private MyAdapter<CheckedFriend> getAdapter() {

        adapter = MyAdapter.<CheckedFriend>newInstance()
                .onGetItemViewType(item -> 0)
                .onInflate(viewType -> R.layout.layout_select_item)
                .onCreateView(holder -> {
                    holder.circleImageView = new CircleImageView[1];
                    holder.checkBox = new CheckBox[1];
                    holder.textView = new TextView[1];
                    holder.circleImageView[0] = holder.item.findViewById(R.id.avatar_view);
                    holder.checkBox[0] = holder.item.findViewById(R.id.check_box);
                    holder.textView[0] = holder.item.findViewById(R.id.name_view);
                })
                .onBindView((holder, item, position) -> {
                    Bitmap bitmap = ImageUtil.loadAvatar(item.getAccount(), 100, 100);
                    if (bitmap != null) {
                        holder.circleImageView[0].setImageBitmap(bitmap);
                    }
                    holder.textView[0].setText(item.getName());
                    holder.checkBox[0].setChecked(item.isChecked());

                    holder.setClickedViews(holder.item);
                    holder.setCheckedBoxs(holder.checkBox[0]);
                    //                    holder.setLongClickedViews(holder.textView[0]);
                })
                .data(presenter.loadFriends(exculdeAccount))
                .onClick((v, item, holder, type, position) -> {
                    holder.checkBox[0].setChecked(!item.isChecked());
                })
                .onCheck((checkBox, item, holder, type, position) -> {
                    item.setChecked(checkBox.isChecked());
                })
                .make();
        return adapter;
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

    @Override
    public FriendSelectorInterface<CheckedFriend> exclude(List<String> exclude) {
        if (exclude == null || exclude.size() == 0) {
            return this;
        }
        this.exculdeAccount.addAll(exclude);
        return this;
    }

    @Override
    public FriendSelector display() {
        show();
        return this;
    }
}