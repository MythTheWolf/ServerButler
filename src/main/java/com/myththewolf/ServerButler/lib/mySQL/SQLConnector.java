package com.myththewolf.ServerButler.lib.mySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class represents a SQL connector
 */
public class SQLConnector {
    /**
     * The connection object to the server
     */
    private Connection connection;
    /**
     * The address to the server
     */
    private String address;
    /**
     * The port to the server
     */
    private int port;
    /**
     * The username used to connect to the server
     */
    private String username;
    /**
     * The password used to connect to the server
     */
    private String password;
    /**
     * The selected database
     */
    private String dbName;

    /**
     * Creates a new SQLConnector, setting the connection properties
     *
     * @param address  The address to the server
     * @param port     The port to the server
     * @param username The username to use to connect to the server
     * @param password The password to use to connect to the server
     * @param DB       The database to select
     */
    public SQLConnector(String address, int port, String username, String password, String DB) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbName = DB;
    }

    /**
     * Gets the already established connection (if present) or makes a new connection
     *
     * @return The connection
     */
    public Connection getConnection() {
        try {
            return ((connection == null) || connection.isClosed()) ? openConnection() : connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Opens a connection to the server
     *
     * @return The connection
     * @throws SQLException If a error occurred while connecting
     */
    private Connection openConnection() throws SQLException {
        connection = DriverManager
                .getConnection("jdbc:mysql://" + address + ":" + port + "/" + dbName, username, password);
        return connection;
    }
}
