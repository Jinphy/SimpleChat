package com.example.jinphy.simplechat.modules.pick_photo;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * DESC:
 * Created by jinphy on 2018/1/9.
 */

public class PickPhotoPresenter implements PickPhotoContract.Presenter {


    private final WeakReference<Context> context;
    private final PickPhotoContract.View view;

    public PickPhotoPresenter(Context context, PickPhotoContract.View view) {
        this.context = new WeakReference<>(context);
        this.view = view;
    }


    @Override
    public void start() {

    }
}
