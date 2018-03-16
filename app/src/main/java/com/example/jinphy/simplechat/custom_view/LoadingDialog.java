package com.example.jinphy.simplechat.custom_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.animate_interpolator.HorizontalLoadingInterpolator;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * DESC:
 * Created by jinphy on 2018/1/1.
 */

public class LoadingDialog extends AlertDialog {

    public static class Builder {
        private Context context;
        private CharSequence title;               // 标题
        private boolean cancelable = false;       // 是否可以取消
        private int spotCount;                    // 圆点动画的个数
        private int spotColor;                    // 圆点的颜色
        private int spotSize;                     // 圆点的大小
        private long duration;                    // 动画持续时间周期
        private int backgroundColor;              // 背景颜色
        private int titleColor;                   // 标题颜色
        private float alpha;                      // 透明度
        private int width;                        // 对话框宽度
        private int height;                       // 对话框宽度
        private int gravity;                      // 对话框的gravity
        private int xOffset;                      // 对话框位置偏移量
        private int yOffset;                      // 对话框位置偏移量
        private int spotsSmoothWidth;             // 圆点动画的水平动画宽度

        private View contentView;                 // 内容View

        private OnCancelListener onCancel;        // 取消回调



        private Builder(Context context) {
            this.context = context;
            this.title = "加载中...";
            this.spotColor = ContextCompat.getColor(context, R.color.colorAccent);
            this.backgroundColor = ContextCompat.getColor(context, R.color.half_alpha_black);
            this.titleColor = ContextCompat.getColor(context, R.color.colorAccent);
            this.spotCount = 4;
            this.spotSize = ScreenUtils.dp2px(context, 4);
            this.duration = DURATION;
            this.alpha = 0.9f;
            this.width = ScreenUtils.dp2px(context, 150);
            this.height = ScreenUtils.dp2px(context, 100);
            this.spotsSmoothWidth = ScreenUtils.dp2px(context, 108);
            this.gravity = Gravity.CENTER;
            this.xOffset = 0;
            this.yOffset = 0;
        }

        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder cancellable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder layout(int widthDp, int heightDp) {
            this.width = ScreenUtils.dp2px(context, widthDp);
            this.height = ScreenUtils.dp2px(context, heightDp);
            return this;
        }

        /**
         * DESC:
         *
         * @param spotSizeDp 单位为dp
         * Created by jinphy, on 2018/1/1, at 16:16
         */
        public Builder spotSize(int spotSizeDp) {
            this.spotSize = ScreenUtils.dp2px(context, spotSizeDp);
            return this;
        }

        /**
         * DESC:
         * Created by jinphy, on 2018/1/1, at 16:19
         */
        public Builder spotColor(int color) {
            this.spotColor = color;
            return this;
        }

        /**
         * DESC:
         * Created by jinphy, on 2018/1/1, at 16:20
         */
        public Builder spotCount(int count) {
            this.spotCount = count;
            return this;
        }

        /**
         * DESC: 设置水平动画圆点的滑动宽度
         * Created by jinphy, on 2018/1/1, at 21:49
         */
        public Builder spotsSmoothWidth(int spotsSmoothWidthDp) {
            this.spotsSmoothWidth = ScreenUtils.dp2px(context, spotsSmoothWidthDp);
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * DESC: 设置当前位置的x轴和y轴的偏移量
         * Created by jinphy, on 2018/1/1, at 21:09
         */
        public Builder offset(int xOffsetDp, int yOffsetDp) {
            this.xOffset = ScreenUtils.dp2px(context, xOffsetDp);
            this.yOffset = ScreenUtils.dp2px(context, yOffsetDp);
            return this;
        }

        public Builder view(View contentView) {
            this.contentView = contentView;
            return this;
        }

        /**
         * DESC: 设置取消回调
         * Created by jinphy, on 2018/1/1, at 21:55
         */
        public Builder onCancel(OnCancelListener onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public LoadingDialog build() {
            return new LoadingDialog(this);
        }
    }

    private static final int START_DELAY = 150;
    private static final int DURATION = 1500;

    private SpotView[] spotViews;
    private AnimatorPlayer animator;
    private Builder builder;
    private boolean hasDismiss = false;
    private int duration = 300;
    private Animator startAnimator;


    public static Builder builder(Context context) {
        return new Builder(context);
    }


    private LoadingDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
        setCancelable(builder.cancelable);
        if (builder.onCancel != null) {
            setOnCancelListener(builder.onCancel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setLayout(600,300);
        Window window = getWindow();
        final WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.width = builder.width;
        attrs.height = builder.height;
        attrs.alpha = builder.alpha;
        attrs.gravity = builder.gravity;
        attrs.horizontalMargin = builder.xOffset;
        attrs.verticalMargin = builder.yOffset;
        window.setAttributes(attrs);
        initView();
        setCanceledOnTouchOutside(builder.cancelable);
        animator = new AnimatorPlayer(createAnimations());
    }

    private static final String TAG = "LoadingDialog";
    @Override
    protected void onStart() {
        super.onStart();
        if (builder.contentView == null) {
            AnimUtils.just(findViewById(R.id.root_view))
                    .setAlpha(0,1f)
                    .setDuration(duration)
                    .onStart(a -> startAnimator = a)
                    .onEnd(a -> {
                        for (SpotView view : spotViews) {
                            view.setVisibility(View.VISIBLE);
                        }
                        animator.play();
                    })
                    .animate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (builder.contentView == null) {
            animator.stop();
        }
    }

    @Override
    public void dismiss() {
        if (hasDismiss) {
            // 已经关闭，则直接返回
            return;
        }
        // 否则已经在在显示了，则立即关闭
        hasDismiss = true;
        if (startAnimator != null && startAnimator.isRunning()) {
            startAnimator.cancel();
        }
        Window window = getWindow();
        WindowManager.LayoutParams attris = window.getAttributes();
        AnimUtils.just()
                .setFloat(builder.alpha,0)
                .setDuration(duration)
                .onUpdateFloat(animator -> {
                    float animatedValue = (float) animator.getAnimatedValue();
                    attris.alpha = animatedValue;
                    window.setAttributes(attris);
                })
                .onEnd(a-> super.dismiss())
                .animate();
    }

    private void initView() {
        if (builder.contentView != null) {
            setContentView(builder.contentView);
            return;
        }
        initDefaultView();
    }

    /**
     * DESC: 设置默认的视图
     * Created by jinphy, on 2018/1/1, at 22:07
     */
    private void initDefaultView() {
        setContentView(R.layout.loading_dialog_layout);
        LinearLayout rootView = findViewById(R.id.root_view);
        rootView.setBackgroundColor(builder.backgroundColor);

        // setup title view
        TextView titleView = findViewById(R.id.title_view);
        titleView.setText(builder.title);
        titleView.setTextColor(builder.titleColor);

        // setup animate view
        FrameLayout spotLayout = findViewById(R.id.spot_layout);
        spotViews = new SpotView[builder.spotCount];
        for (int i = 0; i < spotViews.length; i++) {
            SpotView spotView = new SpotView(getContext());
            spotView.setParentWidth(builder.spotsSmoothWidth)
                    .setColor(builder.spotColor)
                    .setXFactor(-1f)
                    .setVisibility(View.INVISIBLE);
            spotLayout.addView(spotView, builder.spotSize, builder.spotSize);
            spotViews[i] = spotView;
        }
    }

    public void title(CharSequence title) {
        TextView titleView = findViewById(R.id.title_view);
        if (titleView != null) {
            titleView.setText(title);
        }

    }

    private Animator[] createAnimations() {
        Animator[] animators = new Animator[builder.spotCount];
        for (int i = 0; i < spotViews.length; i++) {
            Animator move = ObjectAnimator.ofFloat(spotViews[i], "xFactor", 0, 1);
            move.setDuration(builder.duration);
            move.setInterpolator(new HorizontalLoadingInterpolator());
            move.setStartDelay(START_DELAY * i);
            animators[i] = move;
        }
        return animators;
    }


    /**
     * DESC: 动画播放器
     * Created by jinphy, on 2018/1/1, at 21:03
     */
    static class AnimatorPlayer extends AnimatorListenerAdapter {

        private boolean interrupted = false;
        private AnimatorSet set;
        AnimatorPlayer(Animator[] animators) {
            this.set = new AnimatorSet();
            set.playTogether(animators);
            set.addListener(this);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!interrupted) animate();
        }

        void play() {
            animate();
        }

        void stop() {
            interrupted = true;
        }

        private void animate() {
            set.start();
        }
    }

    static class SpotView extends View {

        private int parentWidth;
        private int color;
        private RectF rectF = new RectF();
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        public SpotView(Context context) {
            super(context);
        }

        public float getXFactor() {
            return getX() / parentWidth;
        }

        public SpotView setXFactor(float xFactor) {
            setX(parentWidth * xFactor);
            return this;
        }

        public SpotView setParentWidth(int parentWidth) {
            this.parentWidth = parentWidth;
            return this;
        }

        public int getParentWidth() {
            return parentWidth;
        }

        public SpotView setColor(int color) {
            this.color = color;
            return this;
        }

        public int getColor(){
            return color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = getWidth();
            rectF.right = radius;
            rectF.bottom = radius;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);
        }
    }
}
