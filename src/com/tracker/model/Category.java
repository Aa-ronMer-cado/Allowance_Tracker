package com.tracker.model;

public class Category {
    private int categoryId;
    private String name;
    private String type;
    private String icon;

    public Category(int categoryId, String name, String type, String icon) {
        this.categoryId = categoryId;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getIcon() { return icon; }
    
    @Override
    public String toString() {
        return icon + " " + name;
    }
}