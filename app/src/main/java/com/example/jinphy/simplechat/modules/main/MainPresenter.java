package com.example.jinphy.simplechat.modules.main;

import android.view.MotionEvent;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;


    public MainPresenter(MainContract.View view) {
        this.view = Preconditions.checkNotNull(view);

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
                                return view.dispatchTouchEvent(event);
                            } else {
                                moveVertical = false;
                                return false;
                            }
                        default:
                            return false;
                    }

                } else if (moveVertical) {
                    return view.dispatchTouchEvent(event);
                } else {
                    return false;
                }

            }


        }

        return false;
    }
}
