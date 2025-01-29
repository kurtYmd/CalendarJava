package com.calendar.utils.comparators;
import com.calendar.models.Contact;

import java.util.Comparator;

public class ContactByNameComparator implements Comparator<String> {
	@Override
	public int compare(String a, String b) {
		return a.compareToIgnoreCase(b);
	}
}
