package com.calendar.services;

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

    public JdbcConnectionSource getConn() throws SQLException {
        return new JdbcConnectionSource(databaseUrl);
    }

}
