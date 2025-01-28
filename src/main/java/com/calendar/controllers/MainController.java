package com.calendar.controllers;

import com.calendar.models.Contact;
import com.calendar.app.Main;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class MainController {

    @FXML
    TableView<Contact> contactsTable;

    @FXML
    Button addContactButton;

    @FXML
    public void addContactAction() {
        String name = JOptionPane.showInputDialog("Enter first name");
        Long phone = null;
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
        Long phone = (long) (100_000_000 + rand.nextInt(900_000_000));

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
    void refreshContactsTable() {
        // Update TableView content
        contactsTable.getItems().setAll();
    }

    @FXML
    static void writeContactsToXML() {
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
