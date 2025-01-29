package com.calendar.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "categories")
public class Category {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField()
    private String name;
    @DatabaseField()
    private String hexColor;

    public Category() {}

    public Category(int id, String name, String hexColor) {
        this.id = id;
        this.name = name;
        setHexColor(hexColor);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.name = name;
    }

    public void setHexColor(String hexColor) {
        if (!hexColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid hex color format. Use #RRGGBB.");
        }
        this.hexColor = hexColor;
    }

    @Override
    public String toString() {
        return "Category{" +
                ", name='" + name + '\'' +
                ", hexColor='" + hexColor + '\'' +
                '}';
    }
}
