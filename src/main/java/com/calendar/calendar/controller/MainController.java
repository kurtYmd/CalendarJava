package com.calendar.calendar.controller;

import com.calendar.calendar.Contact;
import com.calendar.calendar.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import java.util.Random;

public class MainController {

    @FXML
    private TableView<Contact> contactsTable;

    @FXML
    private Button addContactButton;

    @FXML
    public void addContactAction() {
        Random rand = new Random();

        String name = "Contact_" + rand.nextInt(1000);
        Long phone = (long) (100_000_000 + rand.nextInt(900_000_000));

        Contact contact = new Contact(name, phone);

        Main.contacts.add(contact);

        refreshContactsTable();
    }

    private void refreshContactsTable() {
        // Update TableView content
        contactsTable.getItems().setAll();
    }
}
