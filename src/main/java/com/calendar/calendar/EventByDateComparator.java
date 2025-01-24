package com.calendar.calendar;

import java.util.Comparator;

public class EventByDateComparator implements Comparator<Event> {
	
	public int compare(Event a, Event b) {
		return a.getDate().compareTo(b.getDate());
	}
}
