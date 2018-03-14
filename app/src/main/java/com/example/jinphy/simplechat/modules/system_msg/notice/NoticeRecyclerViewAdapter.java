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

public class NoticeRecyclerViewAdapter extends BaseRecyclerViewAdapter<Message, NoticeRecyclerViewAdapter.ViewHolder> {



    @Override
    public void onBindViewHolder(ViewHolder holder, Message item, int position) {
        holder.title.setText(TextUtils.isEmpty(item.getExtra()) ? "无标题" : item.getExtra());
        holder.content.setText(item.getContent());
        holder.time.setText(StringUtils.formatDate(Long.valueOf(item.getCreateTime())));

        if (item.isNew()) {
            holder.newMsgView.setVisibility(View.VISIBLE);
        } else {
            holder.newMsgView.setVisibility(View.GONE);
        }


        setClick(item, position, 0, holder.itemView);

        setLongClick(item, position, 0, holder.itemView);
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_system_notice_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
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

    public List<Message> getNewMsgAndSetOld() {
        List<Message> result = new LinkedList<>();
        for (Message message : data) {
            if (message.isNew()) {
                message.setNew(false);
                result.add(message);
            }
        }
        return result;
    }
}

