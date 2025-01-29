package com.calendar.controllers;

import com.calendar.models.Contact;
import com.calendar.app.Main;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.*;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
            tab.setContent(null);
        }
    }

}
