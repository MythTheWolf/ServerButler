package com.myththewolf.ServerButler.lib.mySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {
    private Connection connection;
    private String address;
    private int port;
    private String username;
    private String password;
    private String dbName;

    public SQLConnector(String address, int port, String username, String password, String DB) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbName = DB;
    }

    public Connection getConnection() {
        try {
            return ((connection == null) || connection.isClosed()) ? DriverManager
                    .getConnection("jdbc:mysql://" + address + ":" + port + "/"+dbName, username, password) : connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
