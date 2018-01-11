package com.example.jinphy.simplechat.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.listener_adapters.TextListener;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.Keyboard;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 自定义菜单项
 *
 *
 */
public class MenuItemView extends CardView  implements View.OnClickListener{

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PASSWORD = 1;

    private ImageView iconView;
    private TextView titleTextView;
    private View rightLayout;
    private View contentLayout;
    private TextView contentTextView;
    private EditText contentEditText;
    private ImageView arrowView;

    private static WeakReference<MenuItemView> currentFocus;


    /**
     * DESC: 移除当前的焦点
     * Created by jinphy, on 2018/1/8, at 21:26
     */
    public static void removeCurrent() {
        if (ObjectHelper.reference(currentFocus)) {
            currentFocus.get().removeFocus();
        }
    }


    public MenuItemView(Context context) {
        super(context);
        init(null, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        super.setOnClickListener(this);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MenuItemView, defStyle, 0);

        initView();

        initValues(a);

        setupValues();

        a.recycle();
    }

    /**
     * DESC: 初始化View
     * Created by jinphy, on 2018/1/8, at 10:45
     */
    private void initView() {
        if (getChildCount()>0) {
            ObjectHelper.throwRuntime("MenuItemView cannot has any child!");
        }
        View rootView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.layout_menu_item_view, this, true);

        iconView = rootView.findViewById(R.id.icon_view);
        titleTextView = rootView.findViewById(R.id.title_view);
        rightLayout = rootView.findViewById(R.id.right_layout);
        contentLayout = rootView.findViewById(R.id.content_layout);
        contentTextView = rootView.findViewById(R.id.content_text_view);
        contentEditText = rootView.findViewById(R.id.content_edit_text);
        arrowView = rootView.findViewById(R.id.arrow_view);
    }

    /**
     * DESC: 初始化属性值
     * Created by jinphy, on 2018/1/8, at 9:39
     */
    private void initValues(TypedArray a) {
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorAccent);

        color = a.getColor(R.styleable.MenuItemView_color, defaultColor);
        iconColor = a.getColor(R.styleable.MenuItemView_icon_color, color);
        titleTextColor = a.getColor(R.styleable.MenuItemView_title_text_color, color);
        contentTextColor = a.getColor(R.styleable.MenuItemView_content_text_color, color);
        arrowColor = a.getColor(R.styleable.MenuItemView_arrow_color, color);
        if (a.hasValue(R.styleable.MenuItemView_icon_drawable)) {
            iconDrawable = a.getDrawable(R.styleable.MenuItemView_icon_drawable);
        }

        title = a.getString(R.styleable.MenuItemView_title);
        content = a.getString(R.styleable.MenuItemView_content);
        contentHint = a.getString(R.styleable.MenuItemView_content_hint);
        contentType = a.getInt(R.styleable.MenuItemView_content_type, TYPE_TEXT);

        iconSize = a.getDimensionPixelSize(
                R.styleable.MenuItemView_icon_size, ScreenUtils.dp2px(getContext(),24));
        titleTextSize = a.getDimensionPixelSize(
                R.styleable.MenuItemView_title_text_size,ScreenUtils.sp2px(getContext(), 18));
        contentTextSize= a.getDimensionPixelSize(
                R.styleable.MenuItemView_content_text_size,ScreenUtils.sp2px(getContext(),15));


        showRightLayout = a.getBoolean(R.styleable.MenuItemView_show_right_layout, true);
        showArrow = a.getBoolean(R.styleable.MenuItemView_show_arrow, true);
        showInput = a.getBoolean(R.styleable.MenuItemView_show_input, true);
        autoAnimate = a.getBoolean(R.styleable.MenuItemView_auto_animate, true);
        showContent = a.getBoolean(R.styleable.MenuItemView_show_content, true);

        itemBackgroundColor = a.getColor(R.styleable.MenuItemView_item_backgroundColor,
                ContextCompat.getColor(getContext(), R.color.white));
        itemNormalElevation = a.getDimensionPixelSize(R.styleable.MenuItemView_item_normal_elevation,
                ScreenUtils.dp2px(getContext(), 2));
        itemInputElevation = a.getDimensionPixelSize(R.styleable.MenuItemView_item_input_elevation,
                itemNormalElevation+ScreenUtils.dp2px(getContext(), 8));
        itemCornerRadius = a.getDimensionPixelSize(R.styleable.MenuItemView_item_corner_radius,
                ScreenUtils.dp2px(getContext(), 5));

    }

    /**
     * DESC: 设置属性值
     * Created by jinphy, on 2018/1/8, at 10:46
     */
    private void setupValues() {
        // 设置 icon
        ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
        layoutParams.width = iconSize = layoutParams.height = iconSize;
        iconView.setLayoutParams(layoutParams);
        if (iconDrawable != null) {
            iconView.setVisibility(VISIBLE);
            iconView.setImageDrawable(iconDrawable);
            if (iconColor != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconDrawable.setTint(iconColor);
            }
        } else {
            iconView.setVisibility(GONE);
        }

        // 设置 title
        titleTextView.setTextColor(titleTextColor);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        if (title != null) {
            titleTextView.setText(title);
            titleTextView.setVisibility(VISIBLE);
        } else {
            titleTextView.setVisibility(GONE);
        }
        // 设置 content
        if (contentType == TYPE_PASSWORD) {
            contentTextView.setText(toPassword());
            showInput = false;
        } else {
            contentTextView.setText(content);
            contentEditText.setText(content);
        }
        contentTextView.setHint(contentHint);
        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
        contentTextView.setTextColor(contentTextColor);
        contentEditText.setHint(contentHint);
        contentEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
        contentEditText.setTextColor(contentTextColor);
        contentLayout.setVisibility(showContent ? VISIBLE : GONE);
        contentEditText.addTextChangedListener((TextListener.After) editable -> {
            content = editable.toString();
            contentTextView.setText(content);
        });

        // 设置 arrow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arrowView.getDrawable().setTint(arrowColor);
        }
        arrowView.setVisibility(showArrow ? VISIBLE : GONE);

        // 设置 rightLayout
        rightLayout.setVisibility(showRightLayout ? VISIBLE : GONE);

        // 设置 cardView
        setCardBackgroundColor(itemBackgroundColor);
        setCardElevation(itemNormalElevation);
        setRadius(itemCornerRadius);
    }

    //----------------------------------------------------------------------------------------------
    // 外部设置的点击事件
    private OnClick onClickListener;

    /**
     * DESC: 空实现
     *
     * @see #onClick(OnClick)
     * Created by jinphy, on 2018/1/8, at 17:40
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        // no-op
    }

    /**
     * DESC: 更新当前有焦点的MenuItemView
     * Created by jinphy, on 2018/1/8, at 16:36
     */
    @Override
    public void onClick(View v) {
        // 默认点击时是有焦点状态
        boolean hasFocus = true;
        if (ObjectHelper.reference(currentFocus)) {
            // 有焦点存在
            if (currentFocus.get() == this) {
                // 存在的焦点是当前MenuItemView本身，则直接释放焦点
                this.removeFocus();
                hasFocus = false;// 释放焦点
            } else {
                // 存在的焦点是别的MenuItemView，则先释放别的在自己获取焦点
                currentFocus.get().removeFocus();
                this.getFocus();
            }
        } else {
            // 当前没有任何MenuItemView获取焦点，则当前MenuItemView直接获取焦点
            this.getFocus();
        }
        if (onClickListener != null) {
            onClickListener.call(this, hasFocus);
        }
    }


    /**
     * DESC: 捕获焦点
     * Created by jinphy, on 2018/1/8, at 16:36
     */
    private void getFocus() {
        currentFocus = new WeakReference<>(this);
        animateElevation(itemNormalElevation, itemInputElevation);
        if (showRightLayout&& showContent&&showInput) {
            contentTextView.setVisibility(GONE);
            contentEditText.setVisibility(VISIBLE);
            contentEditText.setSelection(content.length());
            Keyboard.open(getContext(), contentEditText);
        }
    }

    /**
     * DESC: 移除焦点
     * Created by jinphy, on 2018/1/8, at 16:37
     */
    private void  removeFocus(){
        currentFocus = null;
        animateElevation(itemInputElevation, itemNormalElevation);
        if (showRightLayout&& showContent&& showInput) {
            this.contentEditText.setVisibility(GONE);
            this.contentTextView.setVisibility(VISIBLE);
            Keyboard.close(getContext(), contentEditText);
        }
    }

    private void animateElevation(int fromElevation, int toElevation) {
        if (!autoAnimate) {
            return;
        }
        AnimUtils.just(this)
                .setFloat(fromElevation,toElevation)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .onUpdateFloat(animator -> this.setCardElevation((Float) animator.getAnimatedValue()))
                .animate();

    }

    //---------属性设置 -----------------------------------------------------------------------------

    /**
     * DESC:
     *
     * <declare-styleable name="MenuItemView">
     *
     *      <attr name="color" format="color"/>
     *      <attr name="icon_color" format="color"/>
     *      <attr name="title_text_color" format="color"/>
     *      <attr name="content_text_color" format="color"/>
     *      <attr name="arrow_color" format="color"/>
     *      <attr name="icon_drawable" format="reference"/>
     *
     *      <attr name="title" format="string"/>
     *      <attr name="content" format="string"/>
     *      <attr name="content_hint" format="string"/>
     *
     *      <attr name="icon_size" format="dimension"/>
     *      <attr name="title_text_size" format="dimension"/>
     *      <attr name="content_text_size" format="dimension"/>
     *      <attr name="content_type" format="enum">
     *          <enum name="text" value="0"/>
     *          <enum name="password" value="1"/>
     *      </attr>
     *
     *      <attr name="show_right_layout" format="boolean"/>
     *      <attr name="show_arrow" format="boolean"/>
     *      <attr name="show_content" format="boolean"/>
     *      <attr name="show_input" format="boolean"/>
     *      <attr name="auto_animate" format="boolean"/>
     *
     *      <attr name="item_backgroundColor" format="color|reference"/>
     *      <attr name="item_corner_radius" format="dimension"/>
     *      <attr name="item_normal_elevation" format="dimension"/>
     *      <attr name="item_input_elevation" format="dimension"/>
     * </declare-styleable>
     *
     *
     * Created by jinphy, on 2018/1/9, at 9:33
     */

    // 所有的颜色：图标颜色、标题颜色、内容颜色、箭头颜色
    private int color;

    // 图标颜色
    private int iconColor;

    // 标题颜色
    private int titleTextColor;

    // 内容颜色
    private int contentTextColor;

    // 箭头颜色
    private int arrowColor;

    // 图片背景
    private Drawable iconDrawable;

    // 标题
    private CharSequence title;

    // 内容
    private CharSequence content;

    // 内容提示
    private CharSequence contentHint;

    // 图标大小
    private int iconSize;

    // 标题大小
    private int titleTextSize;

    // 内容大小
    private int contentTextSize;

    // 是否显示右侧布局，包括内容文本和右侧箭头
    private boolean showRightLayout;

    // 是否显示右侧箭头
    private boolean showArrow;

    // 是否显示内容文本
    private boolean showContent;

    // 当前菜单项的背景颜色
    private int itemBackgroundColor;

    // 当前菜单项正常显示时的高度
    private int itemNormalElevation;

    // 当前菜单项在获得焦点是的高度
    private int itemInputElevation;

    // 当前菜单项的矩形圆角半径
    private int itemCornerRadius;


    /**
     * DESC: 是否在点击时自动显示动画，默认为true
     * Created by jinphy, on 2018/1/9, at 9:16
     */
    private boolean autoAnimate;


    /**
     * DESC: 是否在点击时显示输入框（会弹出键盘），默认为true
     * Created by jinphy, on 2018/1/8, at 17:39
     */
    private boolean showInput = true;

    /**
     * DESC: 文本的内容的类型，有两种
     *
     *  1、正常文本： TYPE_TEXT
     *  2、密码文本： TYPE_PASSWORD
     *
     *  注：如果是密码类型的文本，则输入框是隐藏的，密码的设置智能调用方法手动设置而不能字节从输入框中设置
     * Created by jinphy, on 2018/1/9, at 10:07
     */
    private int contentType;


    public int color() {
        return color;
    }

    /**
     * DESC: 统一设置所有的颜色
     * Created by jinphy, on 2018/1/8, at 9:08
     */
    public MenuItemView color(int color) {
        this.color = color;
        iconColor = titleTextColor = contentTextColor = arrowColor = color;
        setupValues();
        return this;
    }

    public int iconColor() {
        return iconColor;
    }

    /**
     * DESC: 设置图标颜色
     * Created by jinphy, on 2018/1/8, at 9:07
     */
    public MenuItemView iconColor(int iconColor) {
        this.iconColor = iconColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconDrawable.setTint(iconColor);
        }
        return this;
    }

    public int titleTextColor() {
        return titleTextColor;
    }

    /**
     * DESC: 设置标题文本的颜色
     * Created by jinphy, on 2018/1/8, at 9:07
     */
    public MenuItemView titleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        this.titleTextView.setTextColor(titleTextColor);
        return this;
    }

    public int contentTextColor() {
        return contentTextColor;
    }

    /**
     * DESC: 设置内容文本的颜色
     * Created by jinphy, on 2018/1/8, at 9:07
     */
    public MenuItemView contentTextColor(int contentTextColor) {
        this.contentTextColor = contentTextColor;
        this.contentTextView.setTextColor(contentTextColor);
        this.contentEditText.setTextColor(contentTextColor);
        return this;
    }

    public int arrowColor() {
        return arrowColor;
    }

    /**
     * DESC: 设置右侧箭头颜色
     * Created by jinphy, on 2018/1/8, at 9:06
     */
    public MenuItemView arrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.arrowView.getDrawable().setTint(arrowColor);
        }
        return this;
    }

    public Drawable iconDrawable() {
        return iconDrawable;
    }

    /**
     * DESC: 设置图标
     * Created by jinphy, on 2018/1/8, at 9:06
     */
    public MenuItemView iconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
        if (iconColor != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.iconDrawable.setTint(iconColor);
        }
        this.iconView.setImageDrawable(this.iconDrawable);
        return this;
    }


    public CharSequence title() {
        return title;
    }

    /**
     * DESC: 设置标题文本
     * Created by jinphy, on 2018/1/8, at 9:06
     */
    public MenuItemView title(CharSequence title) {
        this.title = title;
        this.titleTextView.setText(title);
        return this;
    }

    public CharSequence content() {
        return content;
    }

    /**
     * DESC: 设置内容文本，如果是密码文本则需要隐藏密码
     * Created by jinphy, on 2018/1/8, at 9:06
     */
    public MenuItemView content(CharSequence content) {
        this.content = content;
        if (contentType == TYPE_PASSWORD) {
            // 匹配任意字符转换成*
            this.contentTextView.setText(content.toString().replaceAll("[\\s\\S]", "*"));
        } else {
            this.contentTextView.setText(content);
            this.contentEditText.setText(content);
        }
        return this;
    }


    public CharSequence contentHint() {
        return contentHint;
    }

    public MenuItemView contentHint(CharSequence contentHint) {
        this.contentHint = contentHint;
        this.contentTextView.setHint(contentHint);
        this.contentEditText.setHint(contentHint);
        return this;
    }

    public int iconSize() {
        return iconSize;
    }

    /**
     * DESC: 设置左侧图标的大小，单位dp
     * Created by jinphy, on 2018/1/8, at 9:03
     */
    public MenuItemView iconSize(int dpValue) {
        this.iconSize = ScreenUtils.dp2px(getContext(), dpValue);
        ViewGroup.LayoutParams layoutParams = this.iconView.getLayoutParams();
        layoutParams.width = layoutParams.height = this.iconSize;
        this.iconView.setLayoutParams(layoutParams);
        return this;
    }

    public int titleTextSize() {
        return titleTextSize;
    }

    /**
     * DESC: 设置标题文本的大小，单位sp
     * Created by jinphy, on 2018/1/8, at 9:03
     */
    public MenuItemView titleTextSize(int spValue) {
        this.titleTextSize = ScreenUtils.sp2px(getContext(), spValue);
        this.titleTextView.setTextSize(this.titleTextSize);
        return this;
    }

    public int contentTextSize() {
        return contentTextSize;
    }

    /**
     * DESC: 设置菜单项内容文本的大小，单位sp
     * Created by jinphy, on 2018/1/8, at 9:02
     */
    public MenuItemView contentTextSize(int spValue) {
        this.contentTextSize = ScreenUtils.sp2px(getContext(), spValue);
        this.contentTextView.setTextSize(this.contentTextSize);
        this.contentEditText.setTextSize(this.contentTextSize);
        return this;
    }

    public int contentType() {
        return contentType;
    }

    private String toPassword() {
        if (content == null) {
            return "";
        }
        return content.toString().replaceAll("[\\s\\S]", "*");
    }

    /**
     * DESC: 设置内容的类型
     * Created by jinphy, on 2018/1/9, at 10:49
     */
    public MenuItemView contentType(int contentType) {
        if (this.contentType == contentType) {
            return this;
        }
        if (contentType == TYPE_PASSWORD) {
            showInput = false;
            contentTextView.setText(toPassword());
        } else {
            contentTextView.setText(content);
            contentEditText.setText(content);
        }
        this.contentType = contentType;
        return this;
    }

    public boolean showRightLayout() {
        return showRightLayout;
    }

    /**
     * DESC: 设置是否显示右侧布局，包括内容文本和右侧箭头
     * Created by jinphy, on 2018/1/9, at 9:20
     */
    public MenuItemView showRightLayout(boolean showRightLayout) {
        this.showRightLayout = showRightLayout;
        this.rightLayout.setVisibility(showRightLayout ? VISIBLE : GONE);
        return this;
    }

    public boolean showArrow() {
        return showArrow;
    }

    public MenuItemView showArrow(boolean showArrow) {
        this.showArrow = showArrow;
        this.arrowView.setVisibility(showArrow?VISIBLE:GONE);
        return this;
    }

    public boolean showContent() {
        return showContent;
    }

    public MenuItemView showContent(boolean showContent) {
        this.showContent = showContent;
        this.contentLayout.setVisibility(showContent?VISIBLE:GONE);
        return this;
    }

    public boolean showInput() {
        return showInput;
    }

    /**
     * DESC: 设置是否在点击时显示输入框，默认为true
     * Created by jinphy, on 2018/1/9, at 9:28
     */
    public MenuItemView showInput(boolean showInput) {
        if (contentType == TYPE_TEXT) {
            this.showInput = showInput;
        } else {
            this.showInput = false;
        }
        return this;
    }

    public int itemBackgroundColor() {
        return itemBackgroundColor;
    }

    public MenuItemView itemBackgroundColor(int itemBackgroundColor) {
        this.itemBackgroundColor = itemBackgroundColor;
        this.setCardBackgroundColor(itemBackgroundColor);
        return this;
    }


    public int itemNormalElevation() {
        return itemNormalElevation;
    }

    public MenuItemView itemNormalElevation(int itemElevation) {
        this.itemNormalElevation = itemElevation;
        this.setCardElevation(itemElevation);
        return this;
    }

    public int itemInputElevation() {
        return itemInputElevation;
    }

    public MenuItemView itemInputElevation(int itemInputElevation) {
        this.itemInputElevation = itemInputElevation;
        return this;
    }

    public int itemCornerRadius() {
        return itemCornerRadius;
    }

    public MenuItemView itemCornerRadius(int itemCornerRadius) {
        this.itemCornerRadius = itemCornerRadius;
        this.setRadius(itemCornerRadius);
        return this;
    }

    public boolean autoAnimate() {
        return autoAnimate;
    }

    public MenuItemView autoAnimate(boolean autoAnimate) {
        this.autoAnimate = autoAnimate;
        return this;
    }



    /**
     * DESC: 监听content文本输入事件
     * Created by jinphy, on 2018/1/8, at 17:35
     */
    public MenuItemView watchContent(TextWatcher watcher) {
        this.contentEditText.addTextChangedListener(watcher);
        return this;
    }

    /**
     * DESC: 点击事件
     * Created by jinphy, on 2018/1/8, at 17:38
     */
    public MenuItemView onClick(OnClick onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }


    public interface OnClick{
        void call(MenuItemView menuItemView, boolean hasFocus);
    }

}
