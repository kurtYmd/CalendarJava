package com.calendar.controllers;

import com.calendar.models.Category;
import com.calendar.app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class CategoryController {

    @FXML
    TableView<Category> categoryTable;

    @FXML
    TableColumn<Category, String> nameColumn;

    @FXML
    TableColumn<Category, String> colorColumn;

    @FXML
    Button addCategoryButton;

    @FXML
    Button editCategoryButton;

    @FXML
    Button deleteCategoryButton;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("hexColor"));

        categoryTable.setItems(Main.tableCategoryList);
    }

    @FXML
    public void addCategoryAction() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Category");
        nameDialog.setHeaderText("Enter category name:");
        Optional<String> nameResult = nameDialog.showAndWait();

        if (!nameResult.isPresent() || nameResult.get().trim().isEmpty()) {
            showError("Invalid Category Name", "Category name cannot be empty.");
            return;
        }

        String categoryName = nameResult.get().trim();

        ChoiceDialog<String> colorDialog = new ChoiceDialog<>(
                "Red",
                Category.PREDEFINED_COLORS.keySet()
        );
        colorDialog.setTitle("Select Color");
        colorDialog.setHeaderText("Choose a color for the category:");

        Optional<String> colorResult = colorDialog.showAndWait();

        if (!colorResult.isPresent()) {
            showError("No Color Selected", "You must select a color.");
            return;
        }

        String selectedColorName = colorResult.get();
        String selectedHexColor = Category.PREDEFINED_COLORS.get(selectedColorName);

        Category newCategory = new Category(categoryName, selectedHexColor);
        Main.tableCategoryList.add(newCategory);
        categoryTable.refresh();
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void editCategoryAction() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showError("Select Category", "Please select a category to edit.");
            return;
        }

        TextInputDialog editDialog = new TextInputDialog(selectedCategory.getName());
        editDialog.setTitle("Edit Category");
        editDialog.setHeaderText("Edit category name:");
        Optional<String> nameResult = editDialog.showAndWait();

        if (nameResult.isPresent()) {
            String newName = nameResult.get().trim();
            if (!newName.isEmpty()) {
                selectedCategory.setName(newName);
            } else {
                showError("Invalid Category Name", "Category name cannot be empty.");
                return;
            }
        }

        String currentColorName = selectedCategory.getColorName();

        ChoiceDialog<String> colorDialog = new ChoiceDialog<>(
                currentColorName,
                Category.PREDEFINED_COLORS.keySet()
        );

        colorDialog.setTitle("Select Color");
        colorDialog.setHeaderText("Choose a new color for the category:");
        Optional<String> colorResult = colorDialog.showAndWait();

        if (colorResult.isPresent()) {
            String newColorName = colorResult.get();
            selectedCategory.setHexColor(Category.PREDEFINED_COLORS.get(newColorName));
        }

        categoryTable.refresh();
    }


    @FXML
    public void deleteCategoryAction() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            Main.tableContactsList.remove(selectedCategory);
        } else {
            showError("Select Category", "Please select a category to delete.");
        }
    }
}
