package com.calendar.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.UUID;

@DatabaseTable(tableName = "contacts")
public class Contact implements Comparable<Contact> {
	@DatabaseField()
	private String name;
	@DatabaseField()
	private long phone;
	private ArrayList<Event> events = new ArrayList<>();
	@DatabaseField(id = true)
	private String id;

	public Contact() {}

	public Contact(String name, long phone) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.phone = phone;
		this.events = new ArrayList();
	}
	
	public Contact(String id, String name, long phone) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.events = new ArrayList();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPhone(long phone) {
		this.phone = phone;
	}
	
	void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
	
	public void addEvent(Event event) {
		if (event != null) {
			this.events.add(event);
		}
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Long getPhone() {
		return phone;
	}
	
	public String getPhoneString() {
		return Long.toString(this.phone);
	}

	public ArrayList<Event> getEvents() {
		return this.events;
	}

	public String getEventsTxt() {
		StringBuilder r = new StringBuilder();
		for (Event e : this.events) {
			r.append(e.toString()).append("\n");
		}
		return r.toString();
	}
	
	public String toString() {
		return getName() + " " + getPhoneString();
	}
	
	public String toString(boolean withEvents) {
		return getName() + " " + getPhoneString() + " Events: " + this.getEvents().toString();
	}
	
	@Override public int compareTo(Contact c) {
        if(this.name.compareTo(c.name) !=0 ) {
        	return this.name.compareTo(c.name);
        } else {
        	return (int) (this.phone - c.phone);
        }
    }
}
