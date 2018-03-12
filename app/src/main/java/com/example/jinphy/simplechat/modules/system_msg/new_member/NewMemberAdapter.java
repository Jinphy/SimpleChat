package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class NewMemberAdapter extends BaseRecyclerViewAdapter<NewMemberAdapter.ViewHolder> {

    private List<Message> messages;

    public NewMemberAdapter() {
        messages = new LinkedList<>();
    }

    public void update(List<Message> messages) {
        this.messages.clear();
        if (messages == null || messages.size() == 0) {
            return;
        }
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_new_member_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO: 2018/3/12  
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        
        
        
        
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

