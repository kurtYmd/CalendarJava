package com.calendar.calendar;

import java.util.Comparator;

public class EventByNameComparator implements Comparator<Event> {
	
	public int compare(Event a, Event b) {
        return a.getName().compareTo(b.getName());
    }
	
}