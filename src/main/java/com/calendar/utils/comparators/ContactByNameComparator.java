package com.calendar.utils.comparators;
import com.calendar.models.Contact;

import java.util.Comparator;

public class ContactByNameComparator implements Comparator<Contact> {
	public int compare(Contact a, Contact b) {
		return a.getName().compareTo(b.getName());
	}
}
