package com.example.jinphy.simplechat.modules.signup;

import android.support.v4.app.Fragment;
import android.view.View;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment<SignUpPresenter> implements SignUpContract.View {

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
            this.presenter = getPresenter();
        }
        this.presenter.start();
    }

//    @Override
//    protected SignUpPresenter getPresenter() {
//        if (callback == null) {
//            throw new NullPointerException(
//                    "the callback cannot be null,you must invoke the fragment.setCallback() method");
//        }
//        return (SignUpPresenter) callback.getPresenter(this);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void registerEvent() {

    }
}
