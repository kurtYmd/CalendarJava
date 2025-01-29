package com.calendar.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@DatabaseTable(tableName = "categories")
public class Category {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField()
    private String name;
    @DatabaseField()
    private String hexColor;


    public static final Map<String, String> PREDEFINED_COLORS = new LinkedHashMap<>();

    static {
        PREDEFINED_COLORS.put("Red", "#FF0000");
        PREDEFINED_COLORS.put("Green", "#008000");
        PREDEFINED_COLORS.put("Blue", "#0000FF");
        PREDEFINED_COLORS.put("Yellow", "#FFFF00");
        PREDEFINED_COLORS.put("Purple", "#800080");
        PREDEFINED_COLORS.put("Orange", "#FFA500");
        PREDEFINED_COLORS.put("Pink", "#FFC0CB");
        PREDEFINED_COLORS.put("Black", "#000000");
        PREDEFINED_COLORS.put("White", "#FFFFFF");
    }

    public void setHexColor(String hexColor) {
        if (!PREDEFINED_COLORS.containsValue(hexColor)) {
            throw new IllegalArgumentException("Invalid hex color selected.");
        }
        this.hexColor = hexColor;
    }

    public String getColorName() {
        return PREDEFINED_COLORS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(hexColor))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");
    }

    public Category() {}

    public Category(String name, String hexColor) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.hexColor = hexColor;
    }

    public Category(String id, String name, String hexColor) {
        this.id = id;
        this.name = name;
        setHexColor(hexColor);
    }

    public String getId() {
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

    @Override
    public String toString() {
        return "Category{" +
                ", name='" + name + '\'' +
                ", hexColor='" + hexColor + '\'' +
                '}';
    }
}
