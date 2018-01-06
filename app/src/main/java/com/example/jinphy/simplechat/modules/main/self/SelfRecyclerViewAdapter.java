package com.example.jinphy.simplechat.modules.main.self;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.models.menu.Self;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 * Created by jinphy on 2017/8/17.
 */

public class SelfRecyclerViewAdapter extends BaseRecyclerViewAdapter<SelfRecyclerViewAdapter.ViewHolder> {

    private List<Self> selfs ;

    public SelfRecyclerViewAdapter(List<Self> selfs) {
        this.selfs = Preconditions.checkNotNull(selfs);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_tab_self_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Self self = selfs.get(position);
        // TODO: 2017/8/17 设置图标和标签
        if (click != null) {
            holder.itemView.setOnClickListener(view -> click.onClick(view,self,0,position));
        }
        if (longClick != null) {
            holder.itemView.setOnLongClickListener(
                    view -> longClick.onLongClick(view,self,0,position));
        }
    }

    @Override
    public int getItemCount() {
        return selfs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView tag;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView.findViewById(R.id.item_view);
            this.icon = itemView.findViewById(R.id.icon_view);
            this.tag = itemView.findViewById(R.id.tag_view);

        }
    }

}
