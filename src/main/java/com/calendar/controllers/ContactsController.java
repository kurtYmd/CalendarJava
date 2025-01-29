package com.calendar.controllers;

import com.calendar.models.Contact;
import com.calendar.app.Main;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private ObservableList<Contact> tableContactsList = FXCollections.observableArrayList();

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

        nameColumn.setComparator(new ContactByNameComparator());
        phoneColumn.setComparator(new ContactByPhoneComparator());

        contactsTable.setItems(tableContactsList);
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
                Main.contacts.add(contact);
                tableContactsList.add(contact);
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

        contactsTable.refresh();
    }

    @FXML
    public void deleteContactAction() {
        Contact selectedContact = contactsTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Main.contacts.remove(selectedContact);
            tableContactsList.remove(selectedContact);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to delete.");
            alert.showAndWait();
        }
    }


    @FXML
    void writeContactsToXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        Document document = builder.newDocument();
        Document documentWithEvents = builder.newDocument();

        Element rootWithEvents = documentWithEvents.createElement("contactsEvents");
        documentWithEvents.appendChild(rootWithEvents);

        Element root = document.createElement("contacts");
        document.appendChild(root);

        Main.contacts.forEach((contact) -> {
            Element contactDoc = document.createElement("contact");
            contactDoc.setAttribute("id", contact.getId());
            contactDoc.setAttribute("name", contact.getName());
            contactDoc.setAttribute("phone", contact.getPhoneString());
            contact.getEvents().forEach((event) -> {
                Element contactWithEventDoc = documentWithEvents.createElement("contactEvent");
                contactWithEventDoc.setAttribute("contactId", contact.getId());
                contactWithEventDoc.setAttribute("eventId", event.getId());
                rootWithEvents.appendChild(contactWithEventDoc);
            });
            root.appendChild(contactDoc);
        });

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        }
        DOMSource source = new DOMSource(document);
        DOMSource sourceWithEvent = new DOMSource(documentWithEvents);

        StreamResult result = new StreamResult("/Users/kurtymd/eclipse-workspace/task2/src/task2/static/contacts.xml");
        StreamResult resultWithEvent = new StreamResult("/Users/kurtymd/eclipse-workspace/task2/src/task2/static/contactsEvents.xml");
        try {
            transformer.transform(source, result);
            transformer.transform(sourceWithEvent, resultWithEvent);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    static void readContactsXML() throws XmlHelperError {

        Document document = XmlHelper.openXmlAsDocument(Main.contactsPath);

        NodeList nodeList = document.getElementsByTagName("contact");
        Main.contacts.clear();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element)nodeList.item(i);
            String id = node.getAttribute("id");
            String name = node.getAttribute("name");
            String phoneTxt = node.getAttribute("phone");
            long phone = Long.parseLong(phoneTxt);
            Contact contact = new Contact(id, name, phone);
            Main.contacts.add(contact);
        }
    }

    @FXML
    static Map<String, ArrayList<String>> readContactsEvents(Boolean reverse) throws XmlHelperError {
        Map<String, ArrayList<String>> contactsEvents = new Hashtable<String, ArrayList<String>>();

        Document contactsEventsDoc = XmlHelper.openXmlAsDocument(Main.contactsEventsPath);

        NodeList nodeList = contactsEventsDoc.getElementsByTagName("contactEvent");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element)nodeList.item(i);

            String eventId = node.getAttribute("eventId");
            String contactId = node.getAttribute("contactId");

            if (reverse) {
                ArrayList<String> elems = contactsEvents.get(eventId);

                if (elems == null) {
                    elems = new ArrayList<>();
                    contactsEvents.put(eventId, elems);
                }
                if(!elems.contains(contactId)) elems.add(contactId);

            } else {
                ArrayList<String> elems = contactsEvents.get(contactId);

                if (elems == null) {
                    elems = new ArrayList<>();
                    contactsEvents.put(contactId, elems);
                }

                if(!elems.contains(eventId)) elems.add(eventId);
            }
        }

        return contactsEvents;
    }

}
