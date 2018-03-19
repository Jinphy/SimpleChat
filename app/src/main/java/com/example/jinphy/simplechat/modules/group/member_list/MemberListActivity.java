package com.example.jinphy.simplechat.modules.group.member_list;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.modules.group.group_detail.ModifyGroupFragment;

public class MemberListActivity extends BaseActivity {


    public static void start(Activity activity, String groupNo) {
        Intent intent = new Intent(activity, MemberListActivity.class);
        intent.putExtra(MemberListFragment.GROUP_NO, groupNo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("群成员");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(MemberListFragment.newInstance(
                getIntent().getStringExtra(MemberListFragment.GROUP_NO)), R.id.fragment));
    }

    @Override
    public MemberListPresenter getPresenter(Fragment fragment) {
        return new MemberListPresenter((MemberListContract.View) fragment);
    }
}
