package com.example.jinphy.simplechat.modules.active_zoom;

import android.util.Log;
import android.view.MotionEvent;

import com.example.jinphy.simplechat.model.Blog;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public class ActiveZonePresenter implements ActiveZoneContract.Presenter {

    private ActiveZoneContract.View view;

    private List<Blog> blogs;


    public ActiveZonePresenter(ActiveZoneFragment view) {
        this.view = Preconditions.checkNotNull(view);

    }

    @Override
    public void start() {

    }

    @Override
    public ActiveZoneRecyclerViewAdapter getAdapter() {
        blogs = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            blogs.add(new Blog());
        }
        return new ActiveZoneRecyclerViewAdapter(blogs);
    }

    @Override
    public int getItemCount() {
        if (blogs != null) {
            return blogs.size();
        }
        return 0;
    }


    private float downX;
    private float downY;
    Boolean moveVertical = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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
                        break;
                }

            } else if (moveVertical) {
                return view.dispatchTouchEvent(event);
            } else {
                return false;
            }

        }


        return false;
    }
}
