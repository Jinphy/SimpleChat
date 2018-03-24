package com.example.jinphy.simplechat.modules.main.routine;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.models.menu.Routine;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class RoutinePresenter implements RoutineContract.Presenter {

    private RoutineContract.View view;

    public RoutinePresenter(@NonNull RoutineContract.View view) {

        this.view = Preconditions.checkNotNull(view);

    }


    @Override
    public void start() {

    }

    @Override
    public List<Routine> loadRoutines() {
        List<Routine> data = new ArrayList<>(9);
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
        return data;
    }
}
