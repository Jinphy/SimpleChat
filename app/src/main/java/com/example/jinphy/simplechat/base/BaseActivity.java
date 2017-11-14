package com.example.jinphy.simplechat.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jinphy.simplechat.utils.Preconditions.checkNotNull;

/**
 * Created by jinphy on 2017/8/9.
 */

public abstract class BaseActivity extends AppCompatActivity {


    private static List<AppCompatActivity> activities = new ArrayList<>();

    protected BaseFragment baseFragment;

    protected static Toast toast;
    protected static Snackbar snackbar;
    protected static String TAG;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        activities.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
    }


    public abstract <T extends BasePresenter> T getPresenter(Fragment fragment) ;


    /**
     *
     * 退出应用
     *
     * */
    public static void exit() {
        for (AppCompatActivity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    /**
     * 往activity中的某个布局容器中添加一个fragment
     *
     * @param fragment 要添加的fragment
     * @param resId 添加fragment的容器的id，一般为某个FrameLayout
     * */
    public Fragment addFragment(@NonNull Fragment fragment, @IdRes int resId) {
        checkNotNull(fragment, "fragment cannot be null");

        String tag = fragment.getClass().getSimpleName();

        FragmentManager manager = getSupportFragmentManager();
        Fragment temp = manager.findFragmentByTag(tag);

        if (temp != null) {
            baseFragment = (BaseFragment) temp;
            return temp;
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(resId, fragment, tag);
        transaction.commit();
        try {
            baseFragment = (BaseFragment) fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;
    }

    /**
     * 往activity中的某个布局容器中替换一个fragment
     *
     * @param newFragment 要替换的fragment
     * @param resId 替换newFragment的容器的id，一般为某个FrameLayout
     * */
    public void replaceFragment(@NonNull Fragment newFragment, @IdRes int resId) {
        checkNotNull(newFragment, "newFragment cannot be null");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(resId, newFragment);
        transaction.commit();
    }

    /**
     * 往activity中的某个布局容器中替换一个fragment，并将被替换的fragment
     * 添加到返回栈中
     *
     * @param newFragment 要替换的fragment
     * @param resId 替换newFragment的容器的id，一般为某个FrameLayout
     * */
    public void replaceFragmentAndAddToBackStack(@NonNull Fragment newFragment, @IdRes int resId) {
        checkNotNull(newFragment, "newFragment cannot be null");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(resId, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * 显示snackBar
     *
     * @param view View对象
     * @param text 要显示的文本内容
     * */
    public  <T> void showSnack(View view, T text) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, text.toString(), Snackbar.LENGTH_LONG);
        } else {
            snackbar.setText(text.toString());
        }
        snackbar.show();
    }

    /**
     * 显示snackBar
     *
     * @param view View对象
     * @param textId 要显示的文本内容的资源id
     * */
    public void showSnack(View view, @StringRes int textId) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, textId, Snackbar.LENGTH_LONG);
        } else {
            snackbar.setText(textId);
        }
        snackbar.show();
    }


    /**
     * 获取snackBar
     *
     * @param view View对象
     * @param text 要显示的文本内容
     * */
    public  <T> Snackbar snackbar(View view, T text) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, text.toString(), Snackbar.LENGTH_LONG);
        } else {
            snackbar.setText(text.toString());
        }

        return snackbar;
    }

    /**
     * 获取snackBar
     *
     * @param view View对象
     * @param textId 要显示的文本内容的资源id
     * */
    public Snackbar snackbar(View view, @StringRes int textId) {
        if (snackbar == null) {
            snackbar = Snackbar.make(view, textId, Snackbar.LENGTH_LONG);
        } else {
            snackbar.setText(textId);
        }

        return snackbar;
    }



    protected boolean handleTouchEvent() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (handleTouchEvent()
                && baseFragment != null
                && dispatchTouchEvent(baseFragment, ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }



    private float downX;
    private float downY;
    private Boolean moveVertical = null;


    boolean dispatchTouchEvent(BaseFragment view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            moveVertical = null;
            view.handleVerticalTouchEvent(event);
            view.handleHorizontalTouchEvent(event);
            return false;
        } else {
            if (moveVertical == null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        view.handleVerticalTouchEvent(event);
                        view.handleHorizontalTouchEvent(event);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getX() - downX);
                        float deltaY = Math.abs(event.getY() - downY);
                        if (deltaY + 3 > deltaX) {
                            moveVertical = true;
                            return view.handleVerticalTouchEvent(event);
                        } else {
                            moveVertical = false;
                            return view.handleHorizontalTouchEvent(event);
                        }
                    default:
                        return false;
                }

            } else if (moveVertical) {
                return view.handleVerticalTouchEvent(event);
            } else {
                return view.handleHorizontalTouchEvent(event);
            }

        }


    }

}
