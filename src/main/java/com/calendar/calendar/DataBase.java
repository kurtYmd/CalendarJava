package com.calendar.calendar;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;

public class DataBase {
    private final String databaseUrl = "jdbc:sqlite:sqlite3.db";
    private final JdbcConnectionSource connectionSource;

    public DataBase() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl);
    }

    String getDatabaseUrl() {
        return databaseUrl;
    }

    JdbcConnectionSource getConn() {
        return connectionSource;
    }

}
