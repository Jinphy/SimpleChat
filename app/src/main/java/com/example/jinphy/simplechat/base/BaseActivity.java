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

    private static Toast toast;
    private static Snackbar snackbar;
    private static String TAG;


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
            return temp;
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(resId, fragment, tag);
        transaction.commit();

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
     * 显示toast
     *
     * @param text 要显示的文本内容
     * */
    public  <T> void showToast(T text) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), text.toString(), Toast.LENGTH_SHORT);
        } else {
            toast.setText(text.toString());
        }
        toast.show();
    }

    /**
     * 显示toast
     *
     * @param textId 要显示的文本内容的资源id
     * */
    public void showToast(@StringRes int textId) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), textId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(textId);
        }
        toast.show();
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



    /**
     * log日志，等级为V
     * */
    public  <T> void v(T msg) {
        if (msg == null) {
            Log.e(TAG, "null");
        } else {
            Log.v(TAG, msg.toString());
        }
    }

    /**
     * log日志，等级为D
     * */
    public  <T> void d(T msg) {
        if (msg == null) {
            Log.e(TAG, "null");
        } else {
            Log.d(TAG, msg.toString());
        }
    }


    /**
     * log日志，等级为I
     * */
    public  <T> void i(T msg) {
        if (msg == null) {
            Log.e(TAG, "null");
        } else {
            Log.i(TAG, msg.toString());
        }

    }


    /**
     * log日志，等级为W
     * */
    public  <T> void w(T msg) {
        if (msg == null) {
            Log.e(TAG, "null");
        } else {
            Log.w(TAG, msg.toString());
        }
    }


    /**
     * log日志，等级为E
     * */
    public  <T> void e(T msg) {
        if (msg == null) {
            Log.e(TAG, "null");
        } else {
            Log.e(TAG, msg.toString());
        }
    }

}
