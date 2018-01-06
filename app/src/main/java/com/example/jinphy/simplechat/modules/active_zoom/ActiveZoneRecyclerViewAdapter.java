package com.example.jinphy.simplechat.modules.active_zoom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.blog.Blog;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public class ActiveZoneRecyclerViewAdapter extends BaseRecyclerViewAdapter<ActiveZoneRecyclerViewAdapter.ViewHolder> {

    List<Blog> blogs;

    public ActiveZoneRecyclerViewAdapter(List<Blog> blogs) {
        this.blogs = Preconditions.checkNotNull(blogs);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.blog_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        // TODO: 2017/8/16 设置头像，昵称，内容等信息

        if (click != null) {
            // TODO: 2017/8/16
        }

        if (longClick != null) {
            // TODO: 2017/8/16
        }
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView name;
        private TextView contentText;
        private GridLayout pictureOrVideoLayout;
        private TextView timeText;
        private View btnComment;

        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar_view);
            this.name = itemView.findViewById(R.id.name_text);
            this.contentText = itemView.findViewById(R.id.content_text);
            this.pictureOrVideoLayout = itemView.findViewById(R.id.picture_or_video_layout);
            this.timeText = itemView.findViewById(R.id.time_text);
            this.btnComment = itemView.findViewById(R.id.btn_comment);
        }
    }
}

