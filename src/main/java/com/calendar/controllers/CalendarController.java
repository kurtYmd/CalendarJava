package com.calendar.controllers;

import com.calendar.app.Main;
import com.calendar.models.Category;
import com.calendar.models.Contact;
import com.calendar.models.Event;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.*;
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
        calendar.getChildren().clear();

        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

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
                        rectangle.setOnMouseClicked(e -> createUpdateEvent(currentDate, null));

                        List<Event> events = calendarEventMap.get(currentDate);
                        if (events != null) {
                            Button eventButton = new Button("Events: " + events.size());
                            eventButton.setOnAction(e -> showEventList(events, currentDate));
                            stackPane.getChildren().add(eventButton);
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

    private void createUpdateEvent(int selectedDate, Event event) {
        // Определяем дату для события
        LocalDate clickedDate;
        LocalTime clickedTime;

        if (selectedDate == -1 && event != null && event.getDate() != null) {
            Instant instant = event.getDate().toInstant();
            clickedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            clickedTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        } else {
            clickedDate = LocalDate.from(dateFocus.withDayOfMonth(selectedDate));
            clickedTime = LocalTime.of(12, 0);
        }

        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Create or Update Event");
        dialog.setHeaderText("Create or update event for: " + clickedDate);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Enter event name");
        if (event != null) {
            eventNameField.setText(event.getName());
        }

        ComboBox<String> eventTimeComboBox = new ComboBox<>();
        eventTimeComboBox.setPromptText("Select Time");
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                eventTimeComboBox.getItems().add(String.format("%02d:%02d", hour, minute));
            }
        }
        if (clickedTime != null) {
            eventTimeComboBox.setValue(String.format("%02d:%02d", clickedTime.getHour(), clickedTime.getMinute()));
        }

        VBox contactsBox = new VBox();
        contactsBox.setSpacing(5);
        List<CheckBox> contactCheckboxes = new ArrayList<>();
        for (Contact contact : Main.tableContactsList) {
            CheckBox checkBox = new CheckBox(contact.toString());
            if (event != null && event.getContacts().contains(contact)) {
                checkBox.setSelected(true);
            }
            contactCheckboxes.add(checkBox);
            contactsBox.getChildren().add(checkBox);
        }

        VBox tagsBox = new VBox();
        tagsBox.setSpacing(5);
        ToggleGroup categoryToggleGroup = new ToggleGroup();
        List<RadioButton> tagRadioButtons = new ArrayList<>();

        for (Category category : Main.tableCategoryList) {
            RadioButton radioButton = new RadioButton(category.getName());
            radioButton.setToggleGroup(categoryToggleGroup);

            if (event != null && category.equals(event.getCategory())) {
                radioButton.setSelected(true);
            }

            tagRadioButtons.add(radioButton);
            tagsBox.getChildren().add(radioButton);
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
                new Label("Event Name:"),
                eventNameField,
                new Label("Event Time:"),
                eventTimeComboBox,
                new Label("Select Contacts:"),
                contactsBox,
                new Label("Tags:"),
                tagsBox
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String timeInput = eventTimeComboBox.getValue();
                if (timeInput == null) {
                    showAlert("No Time Selected", "Please select a time for the event.");
                    return null;
                }
                LocalTime eventTime = LocalTime.parse(timeInput);

                List<Contact> selectedContacts = new ArrayList<>();
                for (int i = 0; i < contactCheckboxes.size(); i++) {
                    if (contactCheckboxes.get(i).isSelected()) {
                        selectedContacts.add(Main.tableContactsList.get(i));
                    }
                }

                if (selectedContacts.isEmpty()) {
                    showAlert("No Contacts Selected", "Please select at least one contact.");
                    return null;
                }

                LocalDateTime ldt = LocalDateTime.of(clickedDate, eventTime);
                Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

                RadioButton selectedRadio = (RadioButton) categoryToggleGroup.getSelectedToggle();
                Category category = null;
                if (selectedRadio != null) {
                    String selectedCategory = selectedRadio.getText();
                    category = Main.tableCategoryList.stream().filter(c -> c.getName().equals(selectedCategory)).findFirst().orElse(null);
                }

                if (event == null) {
                    Event newEvent = new Event(eventNameField.getText(), out);
                    newEvent.setCategory(category);
                    for (Contact contact : selectedContacts) {
                        newEvent.addContact(contact);
                    }
                    return newEvent;
                }
                event.setName(eventNameField.getText());
                event.setDate(out);
                event.setCategory(category);

                event.clearContacts();
                for (Contact contact : selectedContacts) {
                    event.addContact(contact);
                }

                return event;
            }
            return null;
        });

        Optional<Event> result = dialog.showAndWait();
        if (event == null) {
            result.ifPresent(e -> saveEvent(e));
        } else {
            result.ifPresent(e -> drawCalendar());
        }
        Main.writeEventsToXML();
    }


    private void editEvent(Event event) {
        createUpdateEvent(-1, event);
    }

    private void deleteEvent(Event event) {
        Main.events.remove(event);
        Main.writeEventsToXML();
        drawCalendar();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveEvent(Event event) {
        if (event != null) {
            Main.events.add(event);
            drawCalendar();
        }
    }


    private void showEventList(List<Event> events, int selectedDate) {
        LocalDate clickedDate = LocalDate.from(dateFocus.withDayOfMonth(selectedDate));

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("List of Events");
        dialog.setHeaderText("All events for selected date: " + clickedDate);

        VBox eventBox = new VBox();
        eventBox.setSpacing(10);

        for (Event event : events) {
            VBox eventDetailsBox = new VBox();
            eventDetailsBox.setSpacing(5);

            Label eventLabel = new Label("Event: " + event.getName() + " at " + event.getTxtDate());
            eventDetailsBox.getChildren().add(eventLabel);

            if (!event.getContacts().isEmpty()) {
                Label contactsLabel = new Label("Contacts:");
                eventDetailsBox.getChildren().add(contactsLabel);

                for (Contact contact : event.getContacts()) {
                    Label contactLabel = new Label("- " + contact.getName() + " (" + contact.getPhone() + ")");
                    eventDetailsBox.getChildren().add(contactLabel);
                }
            } else {
                Label noContactsLabel = new Label("No contacts linked to this event.");
                eventDetailsBox.getChildren().add(noContactsLabel);
            }

            Label categoryLabel = new Label("Tags:");
            eventDetailsBox.getChildren().add(categoryLabel);

            Category category = event.getCategory();
            if(category != null) {
               Label tagsLabel = new Label(category.getName());
               tagsLabel.setTextFill(Paint.valueOf(category.getHexColor()));
               eventDetailsBox.getChildren().add(tagsLabel);
            } else {
                Label noTagsLabel = new Label("No tags yet.");
                eventDetailsBox.getChildren().add(noTagsLabel);
            }

            HBox eventActionsBox = new HBox();
            eventActionsBox.setSpacing(10);

            Button editButton = new Button("Edit Event");
            editButton.setOnAction(e -> {
                editEvent(event);
                dialog.close();
                Map<Integer, List<Event>> newEventsMap = getCalendarEventsDay(clickedDate.atStartOfDay(ZoneId.systemDefault()));
                List<Event> newEvents = newEventsMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
                Platform.runLater(() -> showEventList(newEvents, selectedDate));
            });
            eventActionsBox.getChildren().add(editButton);

            Button deleteButton = new Button("Delete Event");
            deleteButton.setOnAction(e -> {
                deleteEvent(event);
                dialog.close();
                Map<Integer, List<Event>> newEventsMap = getCalendarEventsDay(clickedDate.atStartOfDay(ZoneId.systemDefault()));
                List<Event> newEvents = newEventsMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
                Platform.runLater(() -> showEventList(newEvents, selectedDate));
            });
            eventActionsBox.getChildren().add(deleteButton);

            eventDetailsBox.getChildren().add(eventActionsBox);
            eventBox.getChildren().add(eventDetailsBox);
        }

        dialog.getDialogPane().setContent(eventBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    private Map<Integer, List<Event>> createCalendarMap(List<Event> events) {
        Map<Integer, List<Event>> eventMap = new HashMap<>();

        for (Event event : events) {
            int eventDate = event.getDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .getDayOfMonth();
            eventMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
        }
        return eventMap;
    }

    private Map<Integer, List<Event>> getCalendarEventsMonth(ZonedDateTime dateFocus) {
        List<Event> events = Main.events;
        int year = dateFocus.getYear();
        int month = dateFocus.getMonthValue();

        List<Event> filteredEvents = events.stream()
            .filter(event -> {
                ZonedDateTime eventDate = event.getDate().toInstant().atZone(ZoneId.systemDefault());
                return eventDate.getYear() == year && eventDate.getMonthValue() == month;
            })
            .collect(Collectors.toList());

        return createCalendarMap(filteredEvents);
    }

    private Map<Integer, List<Event>> getCalendarEventsDay(ZonedDateTime dateFocus) {
        List<Event> events = Main.events;
        int year = dateFocus.getYear();
        int month = dateFocus.getMonthValue();
        int day = dateFocus.getDayOfMonth();

        List<Event> filteredEvents = events.stream()
            .filter(event -> {
                ZonedDateTime eventDate = event.getDate().toInstant().atZone(ZoneId.systemDefault());
                return eventDate.getYear() == year && eventDate.getMonthValue() == month && eventDate.getDayOfMonth() == day;
            })
            .collect(Collectors.toList());

        return createCalendarMap(filteredEvents);
    }
}
