package com.example.jinphy.simplechat.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.modules.chat.ChatRecyclerViewAdapter;

/**
 * Created by jinphy on 2017/8/15.
 */

public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {


    protected OnClickListener click;

    protected OnLongClickListener longClick;

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public abstract int getItemCount();

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
