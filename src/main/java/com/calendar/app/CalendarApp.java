package com.calendar.app;

import com.calendar.models.Category;
import com.calendar.services.DataBase;
import com.calendar.models.EventContact;
import com.calendar.models.Contact;
import com.calendar.models.Event;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.*;

public class CalendarApp {
    private DataBase db;
    private Dao<Event, String> eventDao;
    private Dao<Category, String> categoryDao;
    private Dao<Contact, String> contactDao;
    private Dao<EventContact, String> eventContactDao;

    public CalendarApp() {
        try {
            createTables();
        } catch (Exception e) {
            System.out.println("Warning: Error creating tables");
        }
        try {
            db = new DataBase();
            eventDao = DaoManager.createDao(db.getConn(), Event.class);
            categoryDao = DaoManager.createDao(db.getConn(), Category.class);
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

    public Dao<Category, String> getCategoryDao() { return categoryDao; }

    public Dao<Contact, String> getContactDao() {
        return contactDao;
    }

    public Dao<EventContact, String> getEventContactDao() {
        return eventContactDao;
    }

    void createTables() {
        try {
			TableUtils.createTable(db.getConn(), Category.class);
		} catch (Exception e) {
            //
        }
        try {
            TableUtils.createTable(db.getConn(), Event.class);
        } catch (Exception e) {
            //
        }
        try {
            TableUtils.createTable(db.getConn(), Contact.class);
        } catch (Exception e) {
            //
        }
        try {
            TableUtils.createTable(db.getConn(), EventContact.class);
        }  catch (Exception e) {
            //
        }
    }

    public void saveAll() {
        try {
            for (Category category : Main.tableCategoryList) {
                categoryDao.createOrUpdate(category);
            }
            for (Contact contact : Main.tableContactsList) {
                contactDao.createOrUpdate(contact);
            }
            System.out.println(Main.events);
            for (Event event : Main.events) {
                eventDao.createOrUpdate(event);
            }
            for (Event event : Main.events) {
                for (Contact contact : event.getContacts()) {
                    EventContact eventContact = new EventContact(event, contact);
                    eventContactDao.createOrUpdate(eventContact);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readAll() {
        try {
            Main.tableCategoryList.clear();
            Main.tableContactsList.clear();
            Main.events.clear();

            List<Category> categories = categoryDao.queryForAll();
            Main.tableCategoryList.addAll(categories);

            List<Contact> contacts = contactDao.queryForAll();
            Main.tableContactsList.addAll(contacts);

            List<Event> events = eventDao.queryForAll();
            Main.events.addAll(events);

            List<EventContact> eventContacts = eventContactDao.queryForAll();
            for (EventContact ec : eventContacts) {
                Event eventInner = Main.events.stream()
                        .filter(e -> e.getId().equals(ec.getEvent().getId()))
                        .findFirst()
                        .orElse(null);
                Contact contactInner = Main.tableContactsList.stream()
                        .filter(c -> c.getId().equals(ec.getContact().getId()))
                        .findFirst()
                        .orElse(null);

                if (eventInner != null && contactInner != null) {
                    eventInner.addContact(contactInner);
                    contactInner.addEvent(eventInner);
                }
            }

            System.out.println("✅ All data read from DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnectionSource() {
        try {
            db.getConn().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

