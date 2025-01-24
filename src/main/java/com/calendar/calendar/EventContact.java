package com.calendar.calendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "event_contact")
public class EventContact {
    @DatabaseField(id = true)
    private String id;

    @DatabaseField(foreign = true, canBeNull = false)
    private Event event;

    @DatabaseField(foreign = true, canBeNull = false)
    private Contact contact;

    public EventContact() {}

    public EventContact(Event event, Contact contact) {
        this.id = UUID.randomUUID().toString();
        this.event = event;
        this.contact = contact;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
