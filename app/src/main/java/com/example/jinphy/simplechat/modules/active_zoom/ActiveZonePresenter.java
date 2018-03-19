package com.example.jinphy.simplechat.modules.active_zoom;

import com.example.jinphy.simplechat.models.blog.Blog;
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
    public List<Blog> loadBlogs() {
        blogs = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            blogs.add(new Blog());
        }
        return blogs;
    }
}
