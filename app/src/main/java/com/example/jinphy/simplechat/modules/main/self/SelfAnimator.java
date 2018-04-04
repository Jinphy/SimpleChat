package com.example.jinphy.simplechat.modules.main.self;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ColorUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;
import com.example.jinphy.simplechat.utils.ViewUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.jinphy.simplechat.utils.ScreenUtils.dp2px;

/**
 * DESC:
 * Created by jinphy on 2018/4/3.
 */

public class SelfAnimator {
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private final Context context;
    private final SelfView myView;
    private final SelfFragment fragment;
    private SelfPresenter presenter;


    private float density;
    int distanceVertical;
    float avatarViewScaleDistance = 0.68f;
    float avatarViewTransXDistance;
    float avatarViewTransYDistance;
    float nameTextTransXDistance;
    float sexViewTransXDistance;
    int baseTransY;
    float oldY;

    boolean hasActionMove = false;

    private int moveOrientation;

    private float deltaY;

    private SelfAnimator(Context context, SelfView selfView, SelfFragment fragment) {
        this.context = context;
        this.myView = selfView;
        this.fragment = fragment;
        this.presenter = fragment.getPresenter();

        density = ScreenUtils.getDensity(context);

        distanceVertical = dp2px(IntConst.HEAD_VIEW_HEIGHT - IntConst.TOOLBAR_HEIGHT, density);
        baseTransY = -distanceVertical;
        avatarViewTransXDistance = dp2px(IntConst.NEGATIVE_150, density);
        avatarViewTransYDistance = dp2px(IntConst.POSITIVE_120, density);
        nameTextTransXDistance = dp2px(IntConst.POSITIVE_50, density);
        sexViewTransXDistance = nameTextTransXDistance;
    }

    public static SelfAnimator init(Context context, SelfView selfView, SelfFragment fragment) {
        return new SelfAnimator(context, selfView, fragment);
    }


    public boolean handleVerticalTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                moveOrientation = VERTICAL;
                hasActionMove = true;
                deltaY = event.getY() - oldY;
                oldY = event.getY();
                if (deltaY < 0 && canMoveUp()) {
                    // 向上滑动
                    moveVertical(getVerticalMoveFactor(deltaY));
                    return true;
                } else if (deltaY > 0 && canMoveDown()) {
                    // 向下滑动
                    moveVertical(getVerticalMoveFactor(deltaY));
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (hasActionMove && moveOrientation == VERTICAL) {
                    hasActionMove = false;
                    float factor = -myView.headView.getTranslationY() * 1.0f / distanceVertical;
                    if (deltaY < 0) {
                        // 向上滑动
                        if (factor > 1.0f / 4) {
                            // 收起
                            animateVertical(factor, 1);
                            presenter.setNeedMoveUp(true);
                        } else {
                            // 展开
                            animateVertical(factor, 0);
                            presenter.setNeedMoveUp(false);
                        }
                    } else {
                        //向下滑动
                        if (factor < 3.0f / 4) {
                            // 展开
                            animateVertical(factor, 0);
                            presenter.setNeedMoveUp(false);
                        } else {
                            // 收起
                            animateVertical(factor, 1);
                            presenter.setNeedMoveUp(true);
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }


    public void onViewPagerScrolled(int position, float offset, int offsetPixels) {
        if (position == 2) {
            if (presenter == null) {
                return;
            }
            if (presenter.needMoveUp()) {
                moveVertical(1);
                ViewUtils.setAlpha(offset, myView.avatarView, myView.nameText);
            } else {
                moveVertical(1 - offset);
            }
        }
    }


    /**
     * DESC: 判断是否可以向上移动
     * Created by jinphy, on 2018/1/11, at 14:12
     */
    public boolean canMoveUp() {
        float transY = -myView.headView.getTranslationY();
        boolean can = transY < distanceVertical;
        return can;
    }

    /**
     * DESC: 判断是否可以向下移动
     * Created by jinphy, on 2018/1/11, at 14:12
     */
    public boolean canMoveDown() {
        float transY = myView.headView.getTranslationY();
        boolean can = transY < 0;

        int scrollY = myView.scrollView.getScrollY();
        return can && scrollY == 0;
    }


    /**
     * DESC: 手指移动过程中的动画转换
     * Created by jinphy, on 2018/3/12, at 9:08
     */
    public void moveVertical(float faction) {
        Observable.just("onCheck null")
                .map(this::checkViewNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isNull -> moveVertical(isNull, faction));
    }

    private boolean checkViewNull(String str) throws Exception {
        int count = 0;
        do {
            if (myView.headView != null) {
                break;
            }
            Thread.sleep(30);
            count++;
        } while (count < 10);

        if (myView.headView != null) {
            return false;
        }
        return true;
    }


    public void moveVertical(boolean isNull, float faction) {
        if (!isNull) {
            myView.headView.setTranslationY(-faction * distanceVertical);
            myView.headView.setCardBackgroundColor(ColorUtils.rgbColorByFactor(
                    fragment.colorPrimaryDark(),
                    fragment.colorPrimary(),
                    faction
            ));
            myView.scrollView.setTranslationY(distanceVertical * (1 - faction));
            ViewUtils.setScaleXY(myView.avatarView, 1 - faction * avatarViewScaleDistance);
            myView.avatarView.setTranslationX(faction * avatarViewTransXDistance);
            myView.avatarView.setTranslationY(faction * avatarViewTransYDistance);
            myView.nameText.setTranslationX(faction * nameTextTransXDistance);
            myView.sexView.setTranslationX(faction * sexViewTransXDistance);
            myView.dateText.setAlpha(1 - faction);
            myView.blurBackground.setAlpha(1 - faction);
        }
    }

    /**
     * DESC: 手指松开后的动画
     * Created by jinphy, on 2018/1/11, at 14:15
     */
    public void animateVertical(float fromFactor, float toFactor) {
        float deltaFactor = Math.abs(toFactor - fromFactor);
        AnimUtils.just()
                .setDuration((long) (IntConst.DURATION_250 * deltaFactor))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setFloat(fromFactor, toFactor)
                .onUpdateFloat(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    moveVertical(value);
                })
                .animate();
    }


    /**
     * DESC: 获取当前headView竖直方向上滑动的百分比
     * Created by jinphy, on 2018/1/11, at 14:14
     */
    private float getVerticalMoveFactor(float deltaY) {
        float transY = myView.headView.getTranslationY();
        transY += deltaY;
        transY = -transY;
        if (deltaY < 0 && transY > distanceVertical) {
            //向上滑动,并且appbarLayout滑到最高上限
            transY = distanceVertical;
        } else if (deltaY > 0 && transY < 0) {
            //向下滑动，并且appbarLayout滑到最低下限
            transY = 0;
        }

        float faction = transY / distanceVertical;

        return faction;
    }

}
