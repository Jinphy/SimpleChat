package com.example.jinphy.simplechat.modules.login;

import android.os.Bundle;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment loginFragment = LoginFragment.newInstance();
        addFragment(loginFragment,R.id.fragment);
    }
}
