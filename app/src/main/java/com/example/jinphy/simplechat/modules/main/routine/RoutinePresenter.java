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

}
