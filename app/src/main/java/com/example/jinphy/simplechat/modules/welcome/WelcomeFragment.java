package com.example.jinphy.simplechat.modules.welcome;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.modules.main.MainActivity;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.constants.IntConst;
import com.example.jinphy.simplechat.modules.login.LoginActivity;
import com.example.jinphy.simplechat.modules.signup.SignUpActivity;
import com.example.jinphy.simplechat.utils.AnimUtils;
import com.example.jinphy.simplechat.utils.ScreenUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an app of this fragment.
 */
public class WelcomeFragment extends BaseFragment<WelcomePresenter> implements WelcomeContract.View {

    private ImageView startView;

    private View loginView;

    private View signUpView;

    private View appNameView;

    private View btnLayout;


    private boolean hasLoaded = false;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new app of
     * this fragment using the provided parameters.
     *
     * @return A new app of fragment WelcomeFragment.
     */
    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    protected int getResourceId() {
        return R.layout.fragment_welcome;
    }

    @Override
    public void initData() {
    }

    @Override
    protected void findViewsById(View view) {

        startView = view.findViewById(R.id.background);
        loginView = view.findViewById(R.id.btn_login);
        signUpView = view.findViewById(R.id.btn_sign_up);
        appNameView = view.findViewById(R.id.app_name);
        btnLayout = view.findViewById(R.id.btn_layout);
    }

    @Override
    protected void setupViews() {

        showAnimator();
        //        ViewTreeObserver observer = view.getViewTreeObserver();
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        //            observer.addOnGlobalLayoutListener( () -> {
        //                if (!hasLoaded) {
        //                    ImageUtil.from(activity)
        //                            .create(R.drawable.pic_start)
        //                            .into(startView);
        //                    hasLoaded = true;
        //                }
        //            });
        //        }
    }

    @Override
    protected void registerEvent() {
        loginView.setOnClickListener(this::showLoginView);
        signUpView.setOnClickListener(this::showSignUpView);
    }

    @Override
    public void showLoginView(View view) {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

    @Override
    public void showSignUpView(View view) {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_main_activity,R.anim.out_welcome_activity);
    }

    @Override
    public void showMainActivity(String account,String password) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        MainActivity.start(getActivity());
        finishActivity();
    }

    @Override
    public void showAnimator() {
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        AnimUtils.just(appNameView)
                .setAlpha(0f,appNameView.getAlpha())
                .setTranY(-100* ScreenUtils.getDensity(getActivity()),0)
                .setDuration(IntConst.DURATION_1500)
                .setInterpolator(interpolator)
                .onEnd(a->presenter.doAfterWelcome(getContext()))
                .animate();
        AnimUtils.just(startView)
                .setAlpha(0f,1f)
                .setScaleX(0,1.5f,1f)
                .setScaleY(0,1.5f,1f)
                .setDuration(IntConst.DURATION_1500)
                .setInterpolator(interpolator)
                .animate();
    }

    @Override
    public void showBtn() {
        AnimUtils.just(btnLayout)
                .setAlpha(0,1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(IntConst.DURATION_1500)
                .onStart(animator -> btnLayout.setVisibility(View.VISIBLE))
                .animate();
    }


}
