package com.example.jinphy.simplechat.base;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.modules.chat.ChatRecyclerViewAdapter;
import com.example.jinphy.simplechat.modules.group.group_list.GroupListAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {


    protected OnClickListener click;

    protected OnLongClickListener longClick;

    protected List<T> data;



    protected abstract void onBindViewHolder(VH holder, T item, int position);

    protected abstract int getResourceId();

    protected abstract VH onCreateViewHolder(View itemView);

    //--------------------------------

    public BaseRecyclerViewAdapter() {
        data = new LinkedList<>();
    }

    public void update(List<T> data) {
        this.data.clear();
        if (data != null && data.size() > 0) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    protected void setClick(T item, int position, int type,  View... views) {
        if (click != null) {
            for (View view : views) {
                view.setOnClickListener(v -> click.onClick(v, item, position, type));
            }
        }
    }

    protected void setLongClick(T item, int position, int type,  View... views) {
        if (longClick != null) {
            for (View view : views) {
                view.setOnLongClickListener(v -> longClick.onLongClick(v, item, position, type));
            }
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(getResourceId(), parent, false);
        return onCreateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position){
        onBindViewHolder(holder, data.get(position), position);
    }


    @Override
    public int getItemCount(){
        return data.size();
    }

    public void onClick(OnClickListener listener) {
        this.click = listener;

    }

    public void onLongClick(OnLongClickListener listener) {
        this.longClick = listener;
    }

    public interface OnClickListener<T> {

        void onClick(View view, T item, int type, int position);
    }

    public interface OnLongClickListener<T> {

        boolean onLongClick(View view, T item,int type,int position);
    }


}
