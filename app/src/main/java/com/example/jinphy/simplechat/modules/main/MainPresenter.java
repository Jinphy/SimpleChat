package com.example.jinphy.simplechat.modules.main;

import android.support.v7.view.menu.MenuView;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;


    public MainPresenter(MainContract.View view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    private float downX;
    private float downY;
    Boolean moveVertical = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (view.currentItemPosition() == 3) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                moveVertical = null;
                view.dispatchTouchEvent(event);
                return false;
            } else {
                if (moveVertical == null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            view.dispatchTouchEvent(event);
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            float deltaX = Math.abs(event.getX() - downX);
                            float deltaY = Math.abs(event.getY() - downY);
                            if (deltaY+3 > deltaX) {
                                moveVertical = true;
                                view.dispatchTouchEvent(event);
                                return true;
                            } else {
                                moveVertical = false;
                                return false;
                            }
                        default:
                            break;
                    }

                } else if (moveVertical) {
                    view.dispatchTouchEvent(event);
                    return true;
                } else {
                    return false;
                }

            }


        }

        return false;
    }
}
