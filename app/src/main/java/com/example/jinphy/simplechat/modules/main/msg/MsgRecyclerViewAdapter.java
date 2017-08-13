package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.model.MsgRecord;
import com.example.jinphy.simplechat.modules.chat.ChatRecyclerViewAdapter;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinphy on 2017/8/10.
 */

public class MsgRecyclerViewAdapter extends RecyclerView.Adapter<MsgRecyclerViewAdapter.ViewHolder> {

    private List<MsgRecord> msgRecords;

    public MsgRecyclerViewAdapter(@NonNull List<MsgRecord> msgRecords) {
        this.msgRecords = Preconditions.checkNotNull(msgRecords);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_tab_msg_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MsgRecord msgRecord = msgRecords.get(position);
        // TODO: 2017/8/10 设置avatar等信息

        if (click != null) {
            holder.avatar.setOnClickListener(view -> click.onClick(view,msgRecord));
            holder.itemView.setOnClickListener(view -> click.onClick(view,msgRecord));
        }
        if (longClick != null) {
            holder.avatar.setOnLongClickListener(view -> longClick.onLongClick(view,msgRecord));
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view,msgRecord));
        }


    }


    @Override
    public int getItemCount() {
        return msgRecords.size();
    }

    private OnClickListener click;
    private OnLongClickListener longClick;

    public MsgRecyclerViewAdapter onClick(OnClickListener listener) {
        this.click = listener;
        return this;
    }

    public MsgRecyclerViewAdapter onLongClick(OnLongClickListener listener) {
        this.longClick = listener;
        return this;
    }


    //===================================================================\\
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView name;
        private TextView lastMsg;
        private TextView time;
        private View itemView;


        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.name = itemView.findViewById(R.id.name);
            this.lastMsg = itemView.findViewById(R.id.last_msg);
            this.time = itemView.findViewById(R.id.time);
            this.itemView = itemView;
        }
    }

    public interface OnClickListener{
        void onClick(View view, MsgRecord item);
    }
    public interface OnLongClickListener{
        boolean onLongClick(View view, MsgRecord item);
    }
}

