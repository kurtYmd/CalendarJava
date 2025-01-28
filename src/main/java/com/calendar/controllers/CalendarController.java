package com.calendar.controllers;

import com.calendar.models.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        List<Event> events = new ArrayList<>();
        int year = dateFocus.getYear();
        int month = dateFocus.getMonth().getValue();

        return createCalendarMap(events);
    }
}
