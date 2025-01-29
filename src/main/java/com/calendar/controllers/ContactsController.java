package com.calendar.controllers;

import com.calendar.models.Contact;
import com.calendar.app.Main;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.calendar.utils.comparators.ContactByNameComparator;
import com.calendar.utils.comparators.ContactByPhoneComparator;

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
import java.util.*;

public class ContactsController {

    @FXML
    TableView<Contact> contactsTable;

    @FXML
    TableColumn<Contact, String> nameColumn;

    @FXML
    TableColumn<Contact, Long> phoneColumn;

    @FXML
    TableColumn<Contact, String> eventColumn;

    @FXML
    Button addContactButton;

    @FXML
    Button editContactButton;

    @FXML
    Button deleteContactButton;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        eventColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventsTxt()));

        nameColumn.setComparator(new ContactByNameComparator());
        phoneColumn.setComparator(new ContactByPhoneComparator());

        contactsTable.setItems(Main.tableContactsList);
    }

    @FXML
    public void addContactAction() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Contact");
        nameDialog.setHeaderText("Enter first name:");
        Optional<String> nameResult = nameDialog.showAndWait();

        if (nameResult.isPresent()) {
            String name = nameResult.get();

            boolean validPhone = false;
            Long phone = null;

            while (!validPhone) {
                TextInputDialog phoneDialog = new TextInputDialog();
                phoneDialog.setTitle("Add Contact");
                phoneDialog.setHeaderText("Enter phone number:");
                Optional<String> phoneResult = phoneDialog.showAndWait();

                if (phoneResult.isPresent()) {
                    try {
                        phone = Long.parseLong(phoneResult.get());
                        validPhone = true;
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Invalid phone number");
                        alert.setContentText("Please enter only numeric values.");
                        alert.showAndWait();
                    }
                } else {
                    break; // User canceled the dialog
                }
            }

            if (phone != null) {
                Contact contact = new Contact(name, phone);
                Main.tableContactsList.add(contact);
                Main.writeContactsToXML();
            }
        }

    }

    @FXML
    public void editContactAction() {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();

        if (selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to edit.");
            alert.showAndWait();
            return;
        }

        TextInputDialog editDialog = new TextInputDialog(selectedContact.getName());
        editDialog.setTitle("Edit Contact");
        editDialog.setHeaderText("Edit contact name:");
        Optional<String> result = editDialog.showAndWait();

        if (result.isPresent()) {
            selectedContact.setName(result.get());
        }

        boolean validPhone = false;

        while (!validPhone) {
            TextInputDialog phoneDialog = new TextInputDialog(selectedContact.getPhone().toString());
            phoneDialog.setTitle("Edit Contact");
            phoneDialog.setHeaderText("Edit contact phone number:");
            Optional<String> phoneResult = phoneDialog.showAndWait();

            if (phoneResult.isPresent()) {
                try {
                    Long newPhone = Long.parseLong(phoneResult.get());
                    selectedContact.setPhone(newPhone);
                    validPhone = true;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Invalid phone number");
                    alert.setContentText("Please enter only numeric values.");
                    alert.showAndWait();
                }
            } else {
                break;
            }
        }

        Main.writeContactsToXML();
        contactsTable.refresh();
    }

    @FXML
    public void deleteContactAction() {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Main.tableContactsList.remove(selectedContact);
            Main.writeContactsToXML();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to delete.");
            alert.showAndWait();
        }
    }

}
