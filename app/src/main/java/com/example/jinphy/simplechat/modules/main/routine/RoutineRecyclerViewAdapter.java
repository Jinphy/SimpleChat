package com.example.jinphy.simplechat.modules.main.routine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseRecyclerViewAdapter;
import com.example.jinphy.simplechat.model.menu.Routine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public class RoutineRecyclerViewAdapter
        extends BaseRecyclerViewAdapter<RoutineRecyclerViewAdapter.ViewHolder> {

    List<Routine> routines;


    public RoutineRecyclerViewAdapter() {
        initRoutines();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_tab_routine_item, parent, false);

        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.icon.setImageResource(routine.getIconId());
        holder.tag.setText(routine.getTagId());
        if (click != null) {
            holder.itemView.setOnClickListener(view -> click.onClick(view,routine,0,position));
        }
        if (longClick != null) {
            holder.itemView.setOnLongClickListener(view -> longClick.onLongClick(view,routine,0,position));
        }

    }

    @Override
    public int getItemCount() {
        return routines.size();
    }


    private void initRoutines() {
        routines = new ArrayList<>(9);
        int[] icons = new int[]{
                R.drawable.ic_active_zoom_24dp,
                R.drawable.ic_translate_24dp,
                R.drawable.ic_credit_card_24dp,
                R.drawable.ic_certificates_24dp,
                R.drawable.ic_scenic_spot_24dp,
                R.drawable.ic_bus_route_24dp,
                R.drawable.ic_food_menu_24dp,
                R.drawable.ic_express_24dp,
                R.drawable.ic_weather_24dp
        };
        int[] tags = new int[]{
                R.string.routine_active_zoom,
                R.string.routine_translate,
                R.string.routine_credit_card_address,
                R.string.routine_certificates,
                R.string.routine_scenic_spot,
                R.string.routine_bus_route,
                R.string.routine_food_menu,
                R.string.routine_express,
                R.string.routine_weather
        };
        for (int i = 0; i < icons.length; i++) {
            routines.add(new Routine(icons[i], tags[i]));
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        ImageView icon;
        TextView tag;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView.findViewById(R.id.item_view);
            this.icon = itemView.findViewById(R.id.icon_view);
            this.tag = itemView.findViewById(R.id.tag_view);
        }
    }



}
