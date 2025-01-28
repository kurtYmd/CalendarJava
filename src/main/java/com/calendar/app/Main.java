package com.calendar.app;

import com.calendar.models.Contact;
import com.calendar.models.Event;
import com.calendar.utils.comparators.ContactByPhoneComparator;
import com.calendar.utils.comparators.EventByDateComparator;
import com.calendar.utils.helpers.XmlHelper;
import com.calendar.utils.helpers.XmlHelperError;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
	
	public static ArrayList<Contact> contacts = new ArrayList<Contact>();
	public static ArrayList<Event> events = new ArrayList<Event>();

	public static String dateFormat = "yyyy-MM-dd HH:mm";
	public static DateFormat dateFormatter = new SimpleDateFormat(Main.dateFormat);

	public static String contactsPath = "/Users/kurtymd/eclipse-workspace/task2/src/task2/static/contacts.xml";
	public static String eventsPath = "/Users/kurtymd/eclipse-workspace/task2/src/task2/static/events.xml";
	public static String contactsEventsPath = "/Users/kurtymd/eclipse-workspace/task2/src/task2/static/contactsEvents.xml";

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

//	static void readContactsXML() throws XmlHelperError {
//
//		Document document = XmlHelper.openXmlAsDocument(Main.contactsPath);
//
//		NodeList nodeList = document.getElementsByTagName("contact");
//		Main.contacts.clear();
//		for (int i = 0; i < nodeList.getLength(); i++) {
//            Element node = (Element)nodeList.item(i);
//            String id = node.getAttribute("id");
//            String name = node.getAttribute("name");
//            String phoneTxt = node.getAttribute("phone");
//			long phone = Long.parseLong(phoneTxt);
//            Contact contact = new Contact(id, name, phone);
//            Main.contacts.add(contact);
//		}
//	}

	static void sortContacts() {
		Main.contacts.sort(null);
	}

	static void sortContactsByComporator(Comparator c) {
		Collections.sort(Main.contacts, c);
	}

//	static void showContatcs() {
//
//		System.out.println(Main.contacts.toString());
//
//	}

	static void writeEventsToXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}

        Document document = builder.newDocument();

		Element root = document.createElement("events");
        document.appendChild(root);

		Main.events.forEach((event) -> {
			Element eventDoc = document.createElement("event");
			eventDoc.setAttribute("id", event.getId());
			eventDoc.setAttribute("name", event.getName());
			eventDoc.setAttribute("date", event.getTxtDate());
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

        StreamResult result = new StreamResult("/Users/kurtymd/eclipse-workspace/task2/src/task2/static/events.xml");
        try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	static void readEventsXML() throws Exception {
        Document document = XmlHelper.openXmlAsDocument(Main.contactsPath);

        NodeList nodeList = document.getElementsByTagName("event");
		Main.events.clear();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element)nodeList.item(i);
            String id = node.getAttribute("id");
			String dateTxt = node.getAttribute("date");
			String name = node.getAttribute("name");
			try {
				Date date = Main.dateFormatter.parse(dateTxt);
				Event event = new Event(id, name, date);
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

//	static void addEvent() {
//
//		String name = JOptionPane.showInputDialog("Enter event name: ");
//		String dateTxt = JOptionPane.showInputDialog("Enter the date: yyyy-MM-dd HH:mm");
//
//		Date date;
//
//		try {
//			date = Main.dateFormatter.parse(dateTxt);
//		} catch (ParseException e) {
//			System.out.println("Incorrect date format");
//			return;
//		}
//
//		Event event = new Event(name, date);
//
//		Main.events.add(event);
//	}
//
//	static void editEvent(int index) {
//		Event event;
//		try {
//			event = Main.events.get(index);
//		} catch (IndexOutOfBoundsException e) {
//			System.out.println("Event is undefined");
//			return;
//		}
//
//		event.setName(name);
//		event.setDate(date);
//	}

//	static void sortEvents() {
//		Main.events.sort(null);
//	}
//
//	static void sortEventsByComporator(Comparator c) {
//		Collections.sort(Main.events, c);
//	}
//
//	public static void showEvents() {
//
//		System.out.println(Main.events.toString());
//
//	}
//
//	private static void linkContactsAndEvents(Map<String, ArrayList<String>> contactToEvents,
//                                               Map<String, ArrayList<String>> eventToContacts) {
//        for (Contact contact : contacts) {
//            ArrayList<String> eventIds = contactToEvents.get(contact.getId());
//            if (eventIds != null) {
//                for (String eventId : eventIds) {
//                    Event event = findEventById(eventId);
//                    if (event != null) {
//                        contact.addEvent(event);
//                        event.addContact(contact);
//                    }
//                }
//            }
//        }
//    }
}
