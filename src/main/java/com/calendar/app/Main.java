package com.calendar.app;

import com.calendar.models.Category;
import com.calendar.models.Contact;
import com.calendar.models.Event;
import com.calendar.utils.comparators.ContactByPhoneComparator;
import com.calendar.utils.comparators.EventByDateComparator;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/calendar/view/calendar.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();
	}
	
	public static ArrayList<Event> events = new ArrayList<Event>();
	public static ObservableList<Contact> tableContactsList = FXCollections.observableArrayList();
	public static ObservableList<Category> tableCategoryList = FXCollections.observableArrayList();

	public static String dateFormat = "yyyy-MM-dd HH:mm";
	public static DateFormat dateFormatter = new SimpleDateFormat(Main.dateFormat);

	public static String contactsPath = "static/contacts.xml";
	public static String categoriesPath = "static/categories.xml";
	public static String eventsPath = "static/events.xml";
	public static String contactsEventsPath = "static/contactsEvents.xml";

	public static void main(String[] args) {

		launch(args);
//		CalendarApp app = new CalendarApp();
//
//		try {
//			//readEventsXML();
//			readContactsXML();
//			Map<String, ArrayList<String>> eventContact = Main.readContactsEvents(true);
//			Map<String, ArrayList<String>> contactEvent = Main.readContactsEvents(false);
//			linkContactsAndEvents(contactEvent, eventContact);
//		} catch (XmlHelperError e) {
//			e.printStackTrace();
//			return;
//		};
//
//		// Edit
//		showEvents();
//		showContatcs();
//
//		System.out.println();
//
//		editEvent(0);
////		editContact(0);
//
//		System.out.println();
//
//		showEvents();
//		showContatcs();
//
//		System.out.println();
//
//		// Sort
//		sortContacts();
//		sortEvents();
//
//		showEvents();
//		showContatcs();
//
//		System.out.println();
//
//		// Sort by comparator
//		sortContactsByComporator(new ContactByPhoneComparator());
//		sortEventsByComporator(new EventByDateComparator());
//
//		showEvents();
//		showContatcs();
//
//		writeEventsToXML();
	}

	public static void readAllData() {
		tableContactsList.clear();
		tableCategoryList.clear();
		events.clear();
		try {
			readContactsXML();
		} catch (XmlHelperError e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			readCategoriesXML();
		} catch (XmlHelperError e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			readEventsXML();
		} catch (XmlHelperError e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			return;
		}
		try {
			Map<String, ArrayList<String>> eventContact = Main.readContactsEvents(true);
			Map<String, ArrayList<String>> contactEvent = Main.readContactsEvents(false);
			linkContactsAndEvents(contactEvent, eventContact);
		} catch (XmlHelperError e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			return;
		}
	}

	public static void writeContactsToXML() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}

		Document document = builder.newDocument();

		Element root = document.createElement("contacts");
		document.appendChild(root);

		Main.tableContactsList.forEach((contact) -> {
			Element contactDoc = document.createElement("contact");
			contactDoc.setAttribute("id", contact.getId());
			contactDoc.setAttribute("name", contact.getName());
			contactDoc.setAttribute("phone", contact.getPhoneString());
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
		StreamResult result = new StreamResult(Main.contactsPath);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static void readContactsXML() throws XmlHelperError, FileNotFoundException {

		Document document = XmlHelper.openXmlAsDocument(Main.contactsPath);

		NodeList nodeList = document.getElementsByTagName("contact");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element)nodeList.item(i);
			String id = node.getAttribute("id");
			String name = node.getAttribute("name");
			String phoneTxt = node.getAttribute("phone");
			long phone = Long.parseLong(phoneTxt);
			Contact contact = new Contact(id, name, phone);
			Main.tableContactsList.add(contact);
		}
	}

	public static void writeCategoriesToXML() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}

		Document document = builder.newDocument();

		Element root = document.createElement("categories");
		document.appendChild(root);

		Main.tableCategoryList.forEach((category) -> {
			Element categoryDoc = document.createElement("category");
			categoryDoc.setAttribute("id", category.getId());
			categoryDoc.setAttribute("name", category.getName());
			categoryDoc.setAttribute("hexColor", category.getHexColor());
			root.appendChild(categoryDoc);
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
		StreamResult result = new StreamResult(Main.categoriesPath);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static void readCategoriesXML() throws XmlHelperError, FileNotFoundException {

		Document document = XmlHelper.openXmlAsDocument(Main.categoriesPath);

		NodeList nodeList = document.getElementsByTagName("category");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element)nodeList.item(i);
			String id = node.getAttribute("id");
			String name = node.getAttribute("name");
			String hexColor = node.getAttribute("hexColor");
			Category category = new Category(id, name, hexColor);
			Main.tableCategoryList.add(category);
		}
	}

	public static void writeEventsToXML() {
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

		Main.events.forEach((event) -> {
			Element eventDoc = document.createElement("event");
			eventDoc.setAttribute("id", event.getId());
			eventDoc.setAttribute("name", event.getName());
			eventDoc.setAttribute("date", event.getTxtDate());
			Category category = event.getCategory();
			if (category != null) {
				eventDoc.setAttribute("categoryId", category.getId());
			}
			event.getContacts().forEach((contact) -> {
				Element contactWithEventDoc = documentWithEvents.createElement("contactEvent");
				contactWithEventDoc.setAttribute("contactId", contact.getId());
				contactWithEventDoc.setAttribute("eventId", event.getId());
				rootWithEvents.appendChild(contactWithEventDoc);
			});
			root.appendChild(eventDoc);
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

		StreamResult result = new StreamResult(Main.eventsPath);
		StreamResult resultWithEvent = new StreamResult(Main.contactsEventsPath);
		try {
			transformer.transform(source, result);
			transformer.transform(sourceWithEvent, resultWithEvent);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static void readEventsXML() throws XmlHelperError, FileNotFoundException {
		Document document = XmlHelper.openXmlAsDocument(Main.eventsPath);

        NodeList nodeList = document.getElementsByTagName("event");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element)nodeList.item(i);
            String id = node.getAttribute("id");
			String dateTxt = node.getAttribute("date");
			String name = node.getAttribute("name");
			String categoryId = node.getAttribute("categoryId");
			Category category = tableCategoryList.stream()
						.filter(x -> x.getId().equals(categoryId))
						.findFirst()
						.orElse(null);
			try {
				Date date = Main.dateFormatter.parse(dateTxt);
				Event event = new Event(id, name, date);
				event.setCategory(category);
				Main.events.add(event);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
	}

	static Event findEventById(String eventId) {
        for (Event event : events) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

	private static Map<String, ArrayList<String>> readContactsEvents(Boolean reverse) throws XmlHelperError, FileNotFoundException {
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

	private static void linkContactsAndEvents(Map<String, ArrayList<String>> contactToEvents,
                                               Map<String, ArrayList<String>> eventToContacts) {
        for (Contact contact : tableContactsList) {
            ArrayList<String> eventIds = contactToEvents.get(contact.getId());
            if (eventIds != null) {
                for (String eventId : eventIds) {
                    Event event = findEventById(eventId);
                    if (event != null) {
                        contact.addEvent(event);
                        event.addContact(contact);
                    }
                }
            }
        }
    }
}
