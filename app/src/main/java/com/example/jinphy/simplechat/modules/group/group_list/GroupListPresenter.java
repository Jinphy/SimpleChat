package com.example.jinphy.simplechat.modules.group.group_list;

import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public class GroupListPresenter implements GroupListContract.Presenter {

    private final GroupListContract.View view;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public GroupListPresenter(GroupListContract.View view) {
        this.view = view;
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }


    @Override
    public void start() {

    }

    @Override
    public List<Group> loadGroups(boolean showSearchResult) {
        User user = userRepository.currentUser();
        if (showSearchResult) {

        }
        return groupRepository.loadLocal(user.getAccount(), showSearchResult);
    }

    @Override
    public void removeSearchedResult() {
        groupRepository.deleteSearch();
    }
}
