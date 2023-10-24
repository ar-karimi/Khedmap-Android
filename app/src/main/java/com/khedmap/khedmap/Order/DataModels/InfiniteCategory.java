package com.khedmap.khedmap.Order.DataModels;


public class InfiniteCategory {

    private int id;
    private boolean hasChild;
    private String icon;
    private String name;
    private String serverId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
