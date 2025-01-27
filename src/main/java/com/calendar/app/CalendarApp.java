package com.calendar.app;

import com.calendar.services.DataBase;
import com.calendar.models.EventContact;
import com.calendar.models.Contact;
import com.calendar.models.Event;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class CalendarApp {
    private DataBase db;
    private Dao<Event, String> eventDao;
    private Dao<Contact, String> contactDao;
    private Dao<EventContact, String> eventContactDao;

    public CalendarApp() {
        try {
            db = new DataBase();
            eventDao = DaoManager.createDao(db.getConn(), Event.class);
            contactDao = DaoManager.createDao(db.getConn(), Contact.class);
            eventContactDao = DaoManager.createDao(db.getConn(), EventContact.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createTables();
    }
    
    public DataBase getDB() {
        return db;
    }

    public Dao<Event, String> getEventDao() {
        return eventDao;
    }

    public Dao<Contact, String> getContactDao() {
        return contactDao;
    }

    public Dao<EventContact, String> getEventContactDao() {
        return eventContactDao;
    }

    void createTables() {
        try {
			TableUtils.createTable(db.getConn(), Event.class);
			TableUtils.createTable(db.getConn(), Contact.class);
			TableUtils.createTable(db.getConn(), EventContact.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}

