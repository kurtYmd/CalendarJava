package com.calendar.controllers;

import com.calendar.app.CalendarApp;
import com.calendar.app.Main;
import com.calendar.models.Event;
import com.calendar.models.Category;
import com.calendar.models.Contact;

import java.util.List;
import java.util.Scanner;

public class ConsoleModeController {
    private CalendarApp app;
    private final Scanner scanner;

    public ConsoleModeController() {
        this.scanner = new Scanner(System.in);
    }

    public void launch() {
        System.out.println("Launching in console mode...");
        CalendarApp app = new CalendarApp();
        app.readAll();
        System.out.println("All data is loaded.");
        app = null;

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    printCalendar();
                    break;
                case 2:
                    printEvents();
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting console mode...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        app = new CalendarApp();
        app.saveAll();
        app = null;
        System.out.println("All data is loaded.");
    }

    public void printMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Print calendar");
        System.out.println("2. Print events");
        System.out.println("3. Exit");
    }

    private void printCalendar() {
        System.out.println("\n=== Calendar Contacts ===");
        if (Main.tableContactsList.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            for (Contact contact : Main.tableContactsList) {
                System.out.println(contact.getName() + " (Phone: " + contact.getPhone() + ")");
            }
        }
    }

    private void printEvents() {
        System.out.println("\n=== Events List ===");
        if (Main.events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            for (Event event : Main.events) {
                System.out.println("Event: " + event.getName() + " | Date: " + event.getDate() + " | Category: " + event.getCategoryId());
            }
        }
    }
}
