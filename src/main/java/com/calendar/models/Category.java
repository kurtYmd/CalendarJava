package com.calendar.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a category for calendar events.
 * Each category has a unique ID, a name, and a predefined color.
 */
@DatabaseTable(tableName = "categories")
public class Category {
    /**
     * Unique identifier for the category.
     */
    @DatabaseField(id = true)
    private String id;

    /**
     * Name of the category.
     */
    @DatabaseField()
    private String name;

    /**
     * Hex color associated with the category.
     */
    @DatabaseField()
    private String hexColor;

    /**
     * Predefined color mappings for categories.
     */
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

    /**
     * Default constructor required by ORMLite.
     */
    public Category() {}

    /**
     * Creates a new category with a random unique ID.
     *
     * @param name     The name of the category.
     * @param hexColor The hex color code for the category.
     */
    public Category(String name, String hexColor) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.hexColor = hexColor;
    }

    /**
     * Creates a new category with a specified ID.
     *
     * @param id       The unique identifier of the category.
     * @param name     The name of the category.
     * @param hexColor The hex color code for the category.
     */
    public Category(String id, String name, String hexColor) {
        this.id = id;
        this.name = name;
        setHexColor(hexColor);
    }

    /**
     * Gets the unique identifier of the category.
     *
     * @return The category ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the category.
     *
     * @return The category name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the hex color code of the category.
     *
     * @return The hex color code.
     */
    public String getHexColor() {
        return hexColor;
    }

    /**
     * Sets the name of the category.
     *
     * @param name The new name of the category.
     * @throws IllegalArgumentException If the name is empty or null.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.name = name;
    }

    /**
     * Sets the hex color of the category.
     *
     * @param hexColor The new hex color code.
     * @throws IllegalArgumentException If the color is not in {@link #PREDEFINED_COLORS}.
     */
    public void setHexColor(String hexColor) {
        if (!PREDEFINED_COLORS.containsValue(hexColor)) {
            throw new IllegalArgumentException("Invalid hex color selected.");
        }
        this.hexColor = hexColor;
    }

    /**
     * Gets the name of the color based on the stored hex value.
     *
     * @return The name of the color, or "Unknown" if not found.
     */
    public String getColorName() {
        return PREDEFINED_COLORS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(hexColor))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");
    }

    /**
     * Returns a string representation of the category.
     *
     * @return The category name and its hex color.
     */
    @Override
    public String toString() {
        return this.getName() + " " + this.getHexColor();
    }
}
