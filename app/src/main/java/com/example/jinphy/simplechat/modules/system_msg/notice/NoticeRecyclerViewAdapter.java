package com.example.jinphy.simplechat.modules.system_msg.notice;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class NoticeRecyclerViewAdapter extends BaseRecyclerViewAdapter<NoticeRecyclerViewAdapter.ViewHolder> {

    List<Message> messages;

    public NoticeRecyclerViewAdapter() {
        this.messages = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_system_notice_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.title.setText(TextUtils.isEmpty(message.getExtra()) ? "无标题" : message.getExtra());
        holder.content.setText(message.getContent());
        holder.time.setText(StringUtils.formatDate(Long.valueOf(message.getCreateTime())));

        if (message.isNew()) {
            holder.newMsgView.setVisibility(View.VISIBLE);
        } else {
            holder.newMsgView.setVisibility(View.GONE);
        }


        if (click != null) {
            holder.itemView.setOnClickListener(view -> click.onClick(view, message,0,position));
        }
        if (longClick != null) {
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view, message,0,position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView time;
        private TextView content;
        private View newMsgView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_view);
            time = itemView.findViewById(R.id.time_view);
            content = itemView.findViewById(R.id.content_view);
            newMsgView = itemView.findViewById(R.id.new_msg_view);
        }
    }

    public void update(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }

    public List<Message> getNewMsgAndSetOld() {
        List<Message> result = new LinkedList<>();
        for (Message message : messages) {
            if (message.isNew()) {
                message.setNew(false);
                result.add(message);
            }
        }
        return result;
    }
}

