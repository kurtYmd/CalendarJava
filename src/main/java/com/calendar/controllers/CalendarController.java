package com.calendar.controllers;

import com.calendar.app.Main;
import com.calendar.models.Contact;
import com.calendar.models.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarController implements Initializable {

    ZonedDateTime dateFocus;
    ZonedDateTime today;

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    private void drawCalendar() {
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        // List of events for a given month
        Map<Integer, List<Event>> calendarEventMap = getCalendarEventsMonth(dateFocus);

        int monthMaxDate = dateFocus.getMonth().maxLength();
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }

        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j + 1) + (7 * i);
                if (calculatedDate > dateOffset) {
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = -(rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);
                        rectangle.setPickOnBounds(false);
                        rectangle.setOnMouseClicked(e -> createUpdateEvent(currentDate));

                        List<Event> events = calendarEventMap.get(currentDate);
                        if (events != null) {
                            createCalendarEvent(events, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void createUpdateEvent(int selectedDate) {
        LocalDate clickedDate = LocalDate.from(dateFocus.withDayOfMonth(selectedDate));
        System.out.println("Clicked date: " + clickedDate);

        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Create or Update Event");
        dialog.setHeaderText("Create or update event for: " + clickedDate);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Enter event name");

        TextField eventTimeField = new TextField();
        eventTimeField.setPromptText("Enter event time (HH:mm)");

        VBox contactsBox = new VBox();
        contactsBox.setSpacing(5);
        List<CheckBox> contactCheckboxes = new ArrayList<>();
        for (Contact contact : Main.contacts) {
            CheckBox checkBox = new CheckBox(contact.getName());
            contactCheckboxes.add(checkBox);
            contactsBox.getChildren().add(checkBox);
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
                new Label("Event Name:"),
                eventNameField,
                new Label("Event Time:"),
                eventTimeField,
                new Label("Select Contacts:"),
                contactsBox
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String timeInput = eventTimeField.getText();
                LocalTime eventTime;
                try {
                    eventTime = LocalTime.parse(timeInput);
                } catch (DateTimeParseException e) {
                    showAlert("Invalid Time", "Please enter a valid time in HH:mm format.");
                    return null;
                }

                List<Contact> selectedContacts = new ArrayList<>();
                for (int i = 0; i < contactCheckboxes.size(); i++) {
                    if (contactCheckboxes.get(i).isSelected()) {
                        selectedContacts.add(Main.contacts.get(i));
                    }
                }

                if (selectedContacts.isEmpty()) {
                    showAlert("No Contacts Selected", "Please select at least one contact.");
                    return null;
                }

                Date in = new Date();
                LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
                Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                Event newEvent = new Event(eventNameField.getText(), out);
                for (Contact contact: selectedContacts) {
                    newEvent.addContact(contact);
                }
                System.out.println("Created event: " + newEvent);

                return newEvent;
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();
        result.ifPresent(event -> {
            saveEvent(event);
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveEvent(Event event) {
        System.out.println("Event saved: " + event);
    }


    private void createCalendarEvent(List<Event> events, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox eventBox = new VBox();
        for (int k = 0; k < events.size(); k++) {
            if (k >= 2) {
                Text moreEvents = new Text("...");
                eventBox.getChildren().add(moreEvents);
                moreEvents.setOnMouseClicked(mouseEvent -> {
                    // On ... click print all events for the given date
                    System.out.println(events);
                });
                break;
            }
            Event event = events.get(k);
            Text text = new Text(event.getName() + ", " + event.getDate());
            eventBox.getChildren().add(text);
            text.setOnMouseClicked(mouseEvent -> {
                // On Text clicked
                System.out.println(text.getText());
            });
        }
        eventBox.setTranslateY((rectangleHeight / 2) * 0.20);
        eventBox.setMaxWidth(rectangleWidth * 0.8);
        eventBox.setMaxHeight(rectangleHeight * 0.65);
        eventBox.setStyle("-fx-background-color:GRAY");
        stackPane.getChildren().add(eventBox);
    }

    private Map<Integer, List<Event>> createCalendarMap(List<Event> events) {
        Map<Integer, List<Event>> eventMap = new HashMap<>();

        for (Event event : events) {
            int eventDate = event.getDate().toInstant().atZone(ZoneId.systemDefault()).getDayOfMonth();
            eventMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
        }

        return eventMap;
    }

    private Map<Integer, List<Event>> getCalendarEventsMonth(ZonedDateTime dateFocus) {
        List<Event> events = Main.events; // Assuming Main.events holds all events
        int year = dateFocus.getYear();
        int month = dateFocus.getMonthValue();

        // Filter events for the specified year and month
        List<Event> filteredEvents = events.stream()
                .filter(event -> {
                    ZonedDateTime eventDate = event.getDate().toInstant().atZone(ZoneId.systemDefault());
                    return eventDate.getYear() == year && eventDate.getMonthValue() == month;
                })
                .collect(Collectors.toList());

        // Map events by day of the month
        return createCalendarMap(filteredEvents);
    }
}
