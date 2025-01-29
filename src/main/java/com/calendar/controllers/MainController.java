package com.calendar.controllers;

import com.calendar.app.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;

public class MainController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab contactsTab;

    @FXML
    private Tab categoriesTab;

    @FXML
    private Tab calendarTab;

    @FXML
    public void showAboutDialog() {
        Dialog aboutDialog = new Dialog();
        aboutDialog.setTitle("About Program");
        aboutDialog.setContentText("Calendar version 1.0");
        aboutDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        aboutDialog.showAndWait();
    }

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == contactsTab) {
                loadTabContent(contactsTab, "/com/calendar/view/contactsTab.fxml");
            } else if (newTab == calendarTab) {
                loadTabContent(calendarTab, "/com/calendar/view/calendarTab.fxml");
            } else if (newTab == categoriesTab) {
                loadTabContent(categoriesTab, "/com/calendar/view/categoryTab.fxml");
            }
        });

        loadTabContent(contactsTab, "/com/calendar/view/contactsTab.fxml");
    }

    private void loadTabContent(Tab tab, String fxmlFile) {
        try {
            Main.readAllData();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
            tab.setContent(null);
        }
    }

}
