package com.example.jinphy.simplechat.modules.main.routine;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.models.menu.Routine;
import com.example.jinphy.simplechat.utils.Preconditions;

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
    public RoutineRecyclerViewAdapter getAdapter() {
        return new RoutineRecyclerViewAdapter();
    }

    @Override
    public void handleRecyclerViewEvent(View view, Object item, int type, int position) {

        Routine routine = (Routine) item;

        switch (routine.getTagId()) {
            case R.string.routine_active_zoom:
                this.view.showActiveZoneActivity();
                break;
            case R.string.routine_translate:
                break;
            case R.string.routine_credit_card_address:
                break;
            case R.string.routine_certificates:
                break;
            case R.string.routine_scenic_spot:
                break;
            case R.string.routine_bus_route:
                break;
            case R.string.routine_food_menu:
                break;
            case R.string.routine_express:
                break;
            case R.string.routine_weather:
                break;

        }
    }
}
