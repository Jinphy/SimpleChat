package com.example.jinphy.simplechat.modules.active_zoom;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;

/**
 * Created by jinphy on 2017/8/15.
 */

public class ActiveZoneRecyclerViewAdapter extends BaseRecyclerViewAdapter<ActiveZoneRecyclerViewAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

