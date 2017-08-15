package com.example.jinphy.simplechat.modules.signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment implements SignUpContract.View {


    private SignUpActivity activity;

    private SignUpContract.Presenter presenter;


    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.presenter == null) {
            this.presenter = activity.getPresenter(this);
        }
        this.presenter.start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity = null;
    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }


    @Override
    protected int getResourceId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void initData() {
    }

    @Override
    protected void findViewsById(View view) {
        this.activity = (SignUpActivity) getActivity();
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {

    }
}
