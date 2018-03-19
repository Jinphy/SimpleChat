package com.example.jinphy.simplechat.modules.active_zoom;

import android.view.MotionEvent;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.blog.Blog;

import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public interface ActiveZoneContract {


    interface View extends BaseView<Presenter> {

        void moveVertical(float factor);

        void moveHorizontal(float factor);

        void animateHorizontal(float fromFactor, float toFactor,boolean exit);

    }



    interface Presenter extends BasePresenter{

        List<Blog> loadBlogs();

    }
}

