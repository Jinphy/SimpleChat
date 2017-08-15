package com.example.jinphy.simplechat.modules.signup;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class SignUpActivity extends BaseActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp);
        actionBar.setTitle(R.string.sign_up);

        SignUpFragment fragment = SignUpFragment.newInstance();
        fragment.setCallback(this::getPresenter);
        addFragment(fragment,R.id.fragment);

        getPresenter(fragment);
    }

    @Override
    public SignUpPresenter getPresenter(Fragment fragment) {
        return new SignUpPresenter((SignUpContract.View) fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
