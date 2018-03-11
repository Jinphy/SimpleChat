package com.example.jinphy.simplechat.modules.group.group_list;

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
import com.example.jinphy.simplechat.modules.modify_user_info.ModifyUserFragment;

public class GroupListActivity extends BaseActivity {


    public static void start(Activity activity,boolean showSearchResult) {
        Intent intent = new Intent(activity, GroupListActivity.class);
        intent.putExtra(GroupListFragment.SHOW_SEARCH_RESULT, showSearchResult);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorPrimary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("群聊");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_24dp);

        getPresenter(addFragment(
                GroupListFragment.newInstance(
                        getIntent().getBooleanExtra(
                                GroupListFragment.SHOW_SEARCH_RESULT,
                                false)),
                R.id.fragment));
    }

    @Override
    public GroupListPresenter getPresenter(Fragment fragment) {
        return new GroupListPresenter((GroupListContract.View) fragment);
    }
}
