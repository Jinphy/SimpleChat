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

public class ActiveZoneRecyclerViewAdapter extends BaseRecyclerViewAdapter<Blog, ActiveZoneRecyclerViewAdapter.ViewHolder> {


    @Override
    public void onBindViewHolder(ViewHolder holder, Blog item, int position) {
        // TODO: 2017/8/16 设置头像，昵称，内容等信息


        // TODO: 2018/3/14 添加具体的View的点击事件
        setClick(item, position, 0);

        // TODO: 2018/3/14 添加具体的View的长按点击事件
        setLongClick(item, position, 0);
    }

    @Override
    protected int getResourceId() {
        return R.layout.blog_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
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

