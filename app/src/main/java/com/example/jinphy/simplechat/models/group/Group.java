package com.example.jinphy.simplechat.models.group;

import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

/**
 * 群聊实体类
 *
 * Created by Jinphy on 2018/3/5.
 */

@Entity
public class Group{

    public static final int DEFAULT_MAX_COUNT = 500;

    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_OK = "ok";
    public static final String STATUS_NO = "no";
    public static final String STATUS_INVALIDATE = "invalidate";

    @Id(assignable = true)
    protected long id;

    /**
     * DESC: 创建者账号
     * Created by Jinphy, on 2018/3/5, at 18:22
     */
    protected String creator;

    /**
     * DESC: 群聊拥有者，即该群聊属于当前登录账号
     * Created by Jinphy, on 2018/3/6, at 9:41
     */
    protected String owner;

    /**
     * DESC: 群名称
     * Created by Jinphy, on 2018/3/5, at 18:22
     */
    protected String name;

    /**
     * DESC: 群号
     * Created by Jinphy, on 2018/3/5, at 18:21
     */
    protected String groupNo;


    /**
     * DESC: 最大成员数量，需要满足：
     *  1、不得小于1
     *  2、不得小于当前成员数
     *  3、不得大于默认最大成员数
     *
     *  @see Group#DEFAULT_MAX_COUNT
     * Created by Jinphy, on 2018/3/5, at 16:28
     */
    protected int maxCount;

    /**
     * DESC: 成员是否可以自动添加
     *
     *  true:新成员申请加入时不需要群主的确认
     *  false：新成员申请加入时需要群主的确认
     *
     *  todo 在新建群聊的时候需要提供接口让群主选择
     * Created by Jinphy, on 2018/3/5, at 16:31
     */
    protected boolean autoAdd = true;

    /**
     * DESC: 是否在群聊天列表中显示成员昵称
     *
     * Created by Jinphy, on 2018/3/5, at 16:59
     */
    protected boolean showMemberName = true;

    /**
     * DESC: 消息免打扰
     * Created by Jinphy, on 2018/3/6, at 11:33
     */
    protected boolean keepSilent = false;

    /**
     * DESC: 拒绝接收群消息
     * Created by Jinphy, on 2018/3/6, at 11:34
     */
    protected boolean rejectMsg = false;

    /**
     * DESC: 是否是来自的搜索结果
     * Created by jinphy, on 2018/3/11, at 10:36
     */
    protected boolean isFromSearch = false;

    /**
     * DESC: 判断是否是当前用户的群聊
     * Created by jinphy, on 2018/3/11, at 12:57
     */
    protected boolean isMyGroup = false;

    /**
     * DESC: 标志该群是否已被群主解散
     * Created by jinphy, on 2018/3/14, at 14:16
     */
    protected boolean isBroke = false;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public boolean isShowMemberName() {
        return showMemberName;
    }

    public void setShowMemberName(boolean showMemberName) {
        this.showMemberName = showMemberName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isKeepSilent() {
        return keepSilent;
    }

    public void setKeepSilent(boolean keepSilent) {
        this.keepSilent = keepSilent;
    }

    public boolean isRejectMsg() {
        return rejectMsg;
    }

    public void setRejectMsg(boolean rejectMsg) {
        this.rejectMsg = rejectMsg;
    }

    public boolean isFromSearch() {
        return isFromSearch;
    }

    public boolean isMyGroup() {
        return isMyGroup;
    }

    public void setMyGroup(boolean myGroup) {
        isMyGroup = myGroup;
    }

    public void setFromSearch(boolean fromSearch) {
        isFromSearch = fromSearch;
    }

    public static List<Group> parse(List<Map<String, String>> maps) {
        LinkedList<Group> groups = new LinkedList<>();
        if (maps == null || maps.size() == 0) {
            return groups;
        }
        for (Map<String, String> map : maps) {
            groups.add(Group.parse(map));
        }
        return groups;
    }

    public static Group parse(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        Group group = new Group();
        group.setGroupNo(map.get(Group_.groupNo.name));
        group.setOwner(map.get(Group_.owner.name));
        group.setCreator(map.get(Group_.creator.name));
        group.setAutoAdd(Boolean.parseBoolean(map.get(Group_.autoAdd.name)));
        group.setMaxCount(Integer.parseInt(map.get(Group_.maxCount.name)));
        group.setName(map.get(Group_.name.name));
        group.setShowMemberName(Boolean.parseBoolean(map.get(Group_.showMemberName.name)));
        group.setKeepSilent(Boolean.parseBoolean(map.get(Group_.keepSilent.name)));
        group.setRejectMsg(Boolean.parseBoolean(map.get(Group_.rejectMsg.name)));
        return group;
    }

    /**
     * DESC: 把所有的传入的groups都设置成temp
     * Created by jinphy, on 2018/3/11, at 10:40
     */
    public static void setOwner(List<Group> groups, String owner) {
        if (groups != null) {
            for (Group group : groups) {
                group.owner = owner;
            }
        }

    }


    /**
     * DESC: 判断是否是群主
     * Created by jinphy, on 2018/3/12, at 10:22
     */
    public boolean isCreator() {
        return StringUtils.equal(creator, owner);
    }


    public boolean isBroke() {
        return isBroke;
    }

    public void setBroke(boolean broke) {
        isBroke = broke;
    }


    public void update(Group newOne) {
        if (newOne == null) {
            return;
        }
        this.setBroke(newOne.isBroke);
        this.setMyGroup(newOne.isMyGroup);
        this.setRejectMsg(newOne.rejectMsg);
        this.setKeepSilent(newOne.keepSilent);
        this.setShowMemberName(newOne.showMemberName);
        this.setAutoAdd(newOne.autoAdd);
        this.setMaxCount(newOne.maxCount);
        this.setName(newOne.name);
        this.setOwner(newOne.owner);
        this.setCreator(newOne.creator);
        this.setFromSearch(newOne.isFromSearch);
        this.setGroupNo(newOne.groupNo);
    }
}
