package com.example.jinphy.simplechat.modules.active_zoom;

import com.example.jinphy.simplechat.model.blog.Blog;
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

    @Override
    public void onBackPressed() {
        view.onBackPressed();
    }
}
