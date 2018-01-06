package com.example.jinphy.simplechat.modules.main.self;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.menu.Self;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class SelfPresenter implements SelfContract.Presenter {
    private SelfContract.View view;

    private List<Self> selfs;


    public SelfPresenter(@NonNull SelfContract.View view) {
        this.view = Preconditions.checkNotNull(view);
    }

    @Override
    public void start() {

    }

    @Override
    public SelfRecyclerViewAdapter getAdapter() {
        selfs = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            selfs.add(new Self());
        }
        return new SelfRecyclerViewAdapter(selfs);
    }

    @Override
    public int getItemCount() {
        return selfs.size();
    }
}
