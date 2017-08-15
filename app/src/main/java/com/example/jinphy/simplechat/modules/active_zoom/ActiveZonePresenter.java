package com.example.jinphy.simplechat.modules.active_zoom;

import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * Created by jinphy on 2017/8/15.
 */

public class ActiveZonePresenter implements ActiveZoneContract.Presenter {

    private ActiveZoneContract.View view;


    public ActiveZonePresenter(ActiveZoneFragment view) {
        this.view = Preconditions.checkNotNull(view);

        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
