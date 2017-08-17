package com.example.jinphy.simplechat.model.menu;

/**
 * Created by jinphy on 2017/8/15.
 */

public class Routine {

    private int iconId;
    private int tagId;

    public Routine(int iconId, int tagId) {
        this.iconId = iconId;
        this.tagId = tagId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
