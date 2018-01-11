package com.example.jinphy.simplechat.modules.main.self;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.menu.Self;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinphy on 2017/8/10.
 */

public class SelfPresenter implements SelfContract.Presenter {
    private SelfContract.View view;

    private List<Self> selfs;

    private WeakReference<Context> context;
    private UserRepository userRepository;


    public SelfPresenter(Context context, @NonNull SelfContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.userRepository = UserRepository.getInstance();
        this.context = new WeakReference<>(context);
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

    @Override
    public User getUser() {
        return userRepository.currentUser();
    }
}
