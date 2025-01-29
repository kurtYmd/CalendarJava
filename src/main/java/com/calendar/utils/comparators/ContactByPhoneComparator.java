package com.calendar.utils.comparators;
import com.calendar.models.Contact;

import java.util.Comparator;

public class ContactByPhoneComparator implements Comparator<Long> {
	@Override
	public int compare(Long a, Long b) {
		return (int) (a - b);
	}
}
