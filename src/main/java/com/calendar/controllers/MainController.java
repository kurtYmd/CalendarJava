package com.calendar.controllers;

import com.calendar.models.Contact;
import com.calendar.app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import javax.swing.*;
import java.util.Random;

public class MainController {

    @FXML
    private TableView<Contact> contactsTable;

    @FXML
    private Button addContactButton;

    @FXML
    public void addContactAction() {
        String name = JOptionPane.showInputDialog("Enter first name");
        long phone = 0;
        boolean validPhone = false;

        while (!validPhone) {
            try {
                String phoneInput = JOptionPane.showInputDialog("Enter phone number");
                phone = Long.parseLong(phoneInput); // Convert String to Long
                validPhone = true; // Exit loop if conversion succeeds
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid phone number. Please enter only numeric values.");
            }
        }

        Contact contact = new Contact(name, phone);

        Main.contacts.add(contact);

        refreshContactsTable();
    }

    @FXML
    static void editContactAction(int index) {
        Random rand = new Random();
        long phone = 100_000_000 + rand.nextInt(900_000_000);

        Contact contact;
        try {
            contact = Main.contacts.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Contact not found");
            return;
        }

        contact.setPhone(phone);
    }

    @FXML
    private void refreshContactsTable() {
        // Update TableView content
        contactsTable.getItems().setAll();
    }
}
