package com.calendar.utils.comparators;

import com.calendar.models.Event;

import java.util.Comparator;

public class EventByNameComparator implements Comparator<Event> {
	@Override
	public int compare(Event a, Event b) {
        return a.getName().compareTo(b.getName());
    }
	
}