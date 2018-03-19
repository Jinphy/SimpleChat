package com.example.jinphy.simplechat.modules.group.group_list;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.group.Group;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public interface GroupListContract  {

    interface View extends BaseView<Presenter> {
    }



    interface Presenter extends BasePresenter{

        List<Group> loadGroups(boolean showSearchResult);

        void removeSearchedResult();
    }
}
