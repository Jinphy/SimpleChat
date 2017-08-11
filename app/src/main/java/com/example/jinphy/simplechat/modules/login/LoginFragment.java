package com.example.jinphy.simplechat.modules.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.modules.signup.SignUpActivity;
import com.example.jinphy.simplechat.utils.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment  implements LoginContract.View{


    private TextInputEditText accountText;
    private TextInputEditText passwordText;
    private View gotoSignUp;
    private FloatingActionButton fab;

    private LoginActivity activity;

    private LoginContract.Presenter presenter;



    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (LoginActivity) getActivity();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        initView(root);

        initData();

        return root;
    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = Preconditions.checkNotNull(presenter);
    }

    @Override
    public void initView(View view) {

        accountText = view.findViewById(R.id.account_text);
        passwordText = view.findViewById(R.id.password_text);
        gotoSignUp = view.findViewById(R.id.goto_sign_up_text);
        gotoSignUp.setOnClickListener(this::showSignUp);
        fab = getActivity().findViewById(R.id.fab_login);
        fab.setOnClickListener(this::fabAction);

    }

    @Override
    public void initData() {

    }

    @Override
    public void showSignUp(View view) {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(intent);
    }

    private void fabAction(View view) {
        activity.showSnack(view,"you click the fab!");
    }
}
