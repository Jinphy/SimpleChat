package com.example.jinphy.simplechat.base;

import android.view.MotionEvent;

/**
 * Created by jinphy on 2017/8/9.
 */

public interface BasePresenter {

    void start();

    Helper HELPER = new Helper();


//    static boolean dispatchTouchEvent(BaseFragment view, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            HELPER.moveVertical = null;
//            view.handleVerticalTouchEvent(event);
//            view.handleHorizontalTouchEvent(event);
//            return false;
//        } else {
//            if (HELPER.moveVertical == null) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        HELPER.downX = event.getX();
//                        HELPER.downY = event.getY();
//                        view.handleVerticalTouchEvent(event);
//                        view.handleHorizontalTouchEvent(event);
//                        return false;
//                    case MotionEvent.ACTION_MOVE:
//                        float deltaX = Math.abs(event.getX() - HELPER.downX);
//                        float deltaY = Math.abs(event.getY() - HELPER.downY);
//                        if (deltaY + 3 > deltaX) {
//                            HELPER.moveVertical = true;
//                            //                            return view.handleVerticalTouchEvent
//                            // (event);
//                        } else {
//                            HELPER.moveVertical = false;
//                            return view.handleHorizontalTouchEvent(event);
//                        }
//                    default:
//                        return false;
//                }
//
//            } else if (HELPER.moveVertical) {
//                //                return view.handleVerticalTouchEvent(event);
//                return false;
//            } else {
//                return view.handleHorizontalTouchEvent(event);
//            }
//
//        }
//
//
//    }


    class Helper {
        float downX;
        private float downY;
        Boolean moveVertical = null;
    }

}
