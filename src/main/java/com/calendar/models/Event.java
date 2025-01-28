package com.calendar.models;

import com.calendar.app.Main;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


@DatabaseTable(tableName = "events")
public class Event implements Comparable<Event> {
	@DatabaseField()
	private String name;
	@DatabaseField(dataType = DataType.DATE_STRING)
	private Date date;
	@DatabaseField(id = true)
	private String id;
	private ArrayList<Contact> contacts;
	
	public Event() {}

	public Event(String name, Date date) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.date = date;
		this.contacts = new ArrayList();
	}

	public Event(String id, String name, Date date) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.contacts = new ArrayList();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}

	public void addContact(Contact contact) {
		this.contacts.add(contact);
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getTxtDate() {
		return Main.dateFormatter.format(this.date);
	}
	
	public ArrayList<Contact> getContacts() {
		return this.contacts;
	}
	
	public String toString() {
		return this.getId() + " " + this.getName() + " " + this.getTxtDate();
	}
	
	public String toString(boolean withContacts) {
		return this.getId() + " " + this.getName() + " " + this.getTxtDate() + " Contacts: " + this.getContacts().toString();
	}

	@Override public int compareTo(Event a)
    {
        if (this.name.compareTo(a.name) != 0) {
            return this.name.compareTo(a.name);
        }
        else {
            return this.date.compareTo(a.date);
        }
    }
}
