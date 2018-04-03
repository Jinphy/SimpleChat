package com.example.jinphy.simplechat.models.menu;

import com.example.jinphy.simplechat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/15.
 */

public final class Routine {

    public final int icon;
    public final int tag;

    private Routine(int icon, int tag) {
        this.icon = icon;
        this.tag = tag;
    }

    public static List<Routine> create() {
        List<Routine> data = new ArrayList<>(9);
        int[] icons = {
                R.drawable.ic_system_24dp,
                R.drawable.ic_group_chat_24dp,
                //                    R.drawable.ic_credit_card_24dp,
                //                    R.drawable.ic_certificates_24dp,
                //                    R.drawable.ic_scenic_spot_24dp,
                //                    R.drawable.ic_bus_route_24dp,
                //                    R.drawable.ic_food_menu_24dp,
                //                    R.drawable.ic_express_24dp,
                //                    R.drawable.ic_weather_24dp
        };
        int[] tags = new int[]{
                R.string.system_msg,
                R.string.routine_group_chat,
                //                    R.string.routine_credit_card_address,
                //                    R.string.routine_certificates,
                //                    R.string.routine_scenic_spot,
                //                    R.string.routine_bus_route,
                //                    R.string.routine_food_menu,
                //                    R.string.routine_express,
                //                    R.string.routine_weather
        };
        for (int i = 0; i < icons.length; i++) {
            data.add(new Routine(icons[i], tags[i]));
        }
        return data;
    }
}
