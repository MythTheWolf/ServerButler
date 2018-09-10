package com.myththewolf.ServerButler.lib.mySQL;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.Bukkit;

import java.sql.*;

/**
 * This class represents a SQL connector
 */
public class SQLConnector implements Loggable {
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
            connection = ((connection == null) || connection.isClosed()) ? openConnection() : connection;
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM `SB_Players` LIMIT 1");
            ResultSet rs = ps.executeQuery();
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            this.connection = openConnection();
        }
        return connection;
    }

    /**
     * Opens a connection to the server
     *
     * @return The connection
     * @throws SQLException If a error occurred while connecting
     */
    private Connection openConnection() {
        debug("Opening new connection");
        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://" + address + ":" + port + "/" + dbName, username, password);
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(ServerButler.plugin);
        }
        return connection;
    }
}
