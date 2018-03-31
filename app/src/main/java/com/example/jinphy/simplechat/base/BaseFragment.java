package com.example.jinphy.simplechat.base;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    public BaseFragment() {
    }

    /**
     * DESC: activity 布局所在的父布局（id为content的系统布局，是一个frameLayout）
     * Created by jinphy, on 2018/1/9, at 8:53
     */
    protected View activityContentView;

    /**
     * DESC: 当前fragment的跟布局
     * Created by jinphy, on 2018/1/9, at 8:54
     */
    protected View rootView;

    protected T presenter;

    private static final String TAG = "BaseFragment";

    private Toast toast;

    protected int colorAccent;
    protected int colorPrimary;
    protected int colorPrimaryDark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        this.presenter = getPresenter();

        int resourceId = getResourceId();

        View root = inflater.inflate(resourceId, container, false);

        initData();

        rootView = root;

        initView(root);

        /**
         * DESC: Must set to true,if you want to use options menu in the fragment
         *
         *
         * Created by jinphy, on 2018/1/9, at 8:44
         */
        setHasOptionsMenu(true);

        return root;
    }

    @SuppressWarnings("all")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        activityContentViewBottom = ScreenUtils.getScreenHeight(getContext());
        keyboardHeight = ScreenUtils.dp2px(getContext(), 230);// 实际的键盘高度不一定是这个，取个大概
        if (presenter == null) {
            presenter = getPresenter();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.presenter == null) {
            this.presenter = getPresenter();
        }
        this.presenter.start();
        this.toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
    }

    private void initView(View view) {

        // 查找所有需要用到的View
        findViewsById(view);

        // 设置Views
        setupViews();

        // 为需要的View注册各种点击事件
        registerEvent();

        // 额外设置
        doExtra();

    }

    private void doExtra(){

        /**
         * DESC: 注册该监听器主要是用来监听键盘是否关闭
         * Created by jinphy, on 2018/1/8, at 21:30
         */
        activityContentView = getActivity().findViewById(android.R.id.content);
        activityContentView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> { //当界面大小变化时，系统就会调用该方法
                            //将当前界面的尺寸传给Rect矩形
                            activityContentView.getWindowVisibleDisplayFrame(rect);
                            //弹起键盘时的变化高度，在该场景下其实就是键盘高度。
                            int deltaHeight = activityContentViewBottom - rect.bottom;
                            if (deltaHeight > keyboardHeight) {
                                // 键盘打开
                                onKeyboardEvent(true);
                            } else if (-deltaHeight > keyboardHeight) {
                                // 键盘关闭
                                onKeyboardEvent(false);
                            }
                            activityContentViewBottom = rect.bottom;
                        });
    }

    protected abstract @LayoutRes int getResourceId();

    protected abstract void initData();

    protected abstract void findViewsById(View view);

    protected abstract void setupViews();

    protected abstract void registerEvent();

    protected T getPresenter() {
        return activity().getPresenter(this);
    }


    /**
     * DESC: 获取当前fragment所在的activity
     * Created by jinphy, on 2018/1/9, at 23:33
     */
    public BaseActivity activity() {
        return (BaseActivity) getActivity();
    }


    public boolean handleHorizontalTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean handleVerticalTouchEvent(MotionEvent event) {
        return false;
    }

    protected void finishActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * DESC: 这个方法在activity的onBackPressed中调用
     *
     *  注：返回true则会执行activity的onBackPressed()
     *
     * Created by jinphy, on 2018/1/8, at 21:03
     */
    public boolean onBackPressed(){
        return true;
    }

    /**
     * DESC: 初始化当前activity的主题颜色
     * Created by jinphy, on 2018/1/9, at 23:35
     */
    protected void initColor() {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) {
            return;
        }
        colorAccent = activity.colorAccent;
        colorPrimary = activity.colorPrimary;
        colorPrimaryDark = activity.colorPrimaryDark;
    }

    public int colorAccent() {
        if (colorAccent == 0) {
            initColor();
        }
        return colorAccent;
    }

    public int colorPrimary() {
        if (colorPrimary == 0) {
            initColor();
        }
        return colorPrimary;
    }

    public int colorPrimaryDark() {
        if (colorPrimaryDark == 0) {
            initColor();
        }
        return colorPrimaryDark;
    }


    //========================================================\\

    @Subscribe
    public void eventBus(String message) {

    }

    public ParamsBuilder newParams() {
        return new ParamsBuilder();
    }


    /**
     * DESC: 网络请求参数构造类，用来添加参数
     * Created by jinphy, on 2018/1/6, at 15:01
     */
    protected static class ParamsBuilder{
        private Map<String,Object> params;

        public ParamsBuilder() {
            this.params = new HashMap<>();
        }

        public ParamsBuilder add(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return params;
        }

    }



    // activity布局所在的系统的 id 为content 的布局的底部位置
    private int activityContentViewBottom;

    // 键盘高度
    private int keyboardHeight;

    private Rect rect = new Rect();


    /**
     * DESC: 键盘状态发生变化是回调
     *
     * @param open true 表示键盘打开，false表示键盘关闭
     * Created by jinphy, on 2018/1/9, at 8:38
     */
    protected void onKeyboardEvent(boolean open) {
        // no-op
    }


}

