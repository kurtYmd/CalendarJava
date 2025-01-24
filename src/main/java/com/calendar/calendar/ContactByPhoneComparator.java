package com.calendar.calendar;
import java.util.Comparator;

public class ContactByPhoneComparator implements Comparator<Contact> {
	public int compare(Contact a, Contact b) {
		return (int) (a.getPhone() - b.getPhone());
	}
}
