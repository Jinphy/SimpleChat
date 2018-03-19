package com.example.jinphy.simplechat.modules.main.routine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseAdapter;
import com.example.jinphy.simplechat.models.menu.Routine;

import java.util.ArrayList;

/**
 * Created by jinphy on 2017/8/15.
 */

public class RoutineRecyclerViewAdapter
        extends BaseAdapter<Routine, RoutineRecyclerViewAdapter.ViewHolder> {


    public RoutineRecyclerViewAdapter() {
        super();
        initRoutines();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Routine routine,  int position) {
        holder.icon.setImageResource(routine.getIconId());
        holder.tag.setText(routine.getTagId());

        setClick(routine, position, 0, holder.itemView);

        setLongClick(routine, position, 0, holder.itemView);
    }

    @Override
    protected int getResourceId(int viewType) {
        return R.layout.main_tab_routine_item;
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }


    private void initRoutines() {
        data = new ArrayList<>(9);
        int[] icons = new int[]{
                R.drawable.ic_active_zoom_24dp,
                R.drawable.ic_group_chat_24dp,
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
                R.string.routine_group_chat,
                R.string.routine_credit_card_address,
                R.string.routine_certificates,
                R.string.routine_scenic_spot,
                R.string.routine_bus_route,
                R.string.routine_food_menu,
                R.string.routine_express,
                R.string.routine_weather
        };
        for (int i = 0; i < icons.length; i++) {
            data.add(new Routine(icons[i], tags[i]));
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
