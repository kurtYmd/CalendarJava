package com.calendar.utils.comparators;

import com.calendar.models.Event;

import java.util.Comparator;

public class EventByDateComparator implements Comparator<Event> {
	
	public int compare(Event a, Event b) {
		return a.getDate().compareTo(b.getDate());
	}
}
