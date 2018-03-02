package com.myththewolf.ServerButler.lib.mySQL;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.logging.Loggable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface holds SQL helper methods
 */
public interface SQLAble extends Loggable {
    /**
     * Gets the SQL current connection
     *
     * @return The SQL connection
     */
    default Connection getSQLConnection() {
        return ServerButler.connector.getConnection();
    }

    /**
     * Prepares a SQL update statement and executes it, throwing any errors
     *
     * @param SQL       The SQL statement to execute
     * @param numParams The total number of parameters
     * @param values    The values to bind to the parameters
     * @throws IllegalStateException If number of parameters != number of values
     * @throws SQLException          If a SQL connection or syntax error occurs
     */
    default void prepareAndExecuteUpdateThrow(String SQL, int numParams, Object... values) throws IllegalStateException, SQLException {
        if (numParams != values.length) {
            throw new IllegalStateException("Num of params do not match values (SQL: " + SQL + ")");
        }
        PreparedStatement preparedStatement = getSQLConnection().prepareStatement(SQL);
        for (int i = 0; i < numParams; i++) {
            Object singleValue = values[i];
            if (singleValue instanceof String) {
                preparedStatement.setString(i + 1, ((String) singleValue));
            } else if (singleValue instanceof Integer) {
                preparedStatement.setInt(i + 1, ((Integer) singleValue));
            } else if (singleValue instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, ((Boolean) singleValue));
            } else if (singleValue != null) {
                preparedStatement.setString(i + 1, singleValue.toString());
            } else {
                preparedStatement.setString(i + 1, null);
            }
        }
        preparedStatement.executeUpdate();
    }

    /**
     * Prepares a SQL statement and executes it, handling errors
     *
     * @param SQL       The SQL statement to execute
     * @param numParams The total number of parameters
     * @param values    The values to bind to the parameters
     */
    default void prepareAndExecuteUpdateExceptionally(String SQL, int numParams, Object... values) {
        try {
            prepareAndExecuteUpdateThrow(SQL, numParams, values);
        } catch (IllegalStateException ex) {
            getLogger().severe("Could not call internal database update: Paramaters mixmatch (SQL: " + SQL + ")");
            ex.printStackTrace();
        } catch (SQLException ex) {
            getLogger().severe("Could not call internal database update: SQL error (SQL: " + SQL + ")");
            ex.printStackTrace();
        }
    }

    /**
     * Prepares a SQL selection statement and executes it, throwing any errors
     *
     * @param SQL       The SQL statement to execute
     * @param numParams The total number of parameters
     * @param values    The values to bind to the parameters
     * @return The result set of the selection
     * @throws IllegalStateException If number of parameters != number of values
     * @throws SQLException          If a SQL connection or syntax error occurs
     */
    default ResultSet prepareAndExecuteSelectThrow(String SQL, int numParams, Object... values) throws IllegalStateException, SQLException {
        if (numParams != values.length) {
            throw new IllegalStateException("Num of params do not match values (SQL: " + SQL + ")");
        }
        PreparedStatement preparedStatement = getSQLConnection().prepareStatement(SQL);
        for (int i = 0; i < numParams; i++) {
            Object singleValue = values[i];
            if (singleValue instanceof String) {
                preparedStatement.setString(i + 1, ((String) singleValue));
            } else if (singleValue instanceof Integer) {
                preparedStatement.setInt(i + 1, ((Integer) singleValue));
            } else if (singleValue instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, ((Boolean) singleValue));
            } else {
                preparedStatement.setString(i + 1, singleValue.toString());
            }
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Prepares a SQL selection statement and executes it, handling errors
     *
     * @param SQL       The SQL statement to execute
     * @param numParams The total number of parameters
     * @param values    The values to bind to the parameters
     * @return The result set of the selection, null if a exception occurs.
     */
    default ResultSet prepareAndExecuteSelectExceptionally(String SQL, int numParams, Object... values) {
        try {
            return prepareAndExecuteSelectThrow(SQL, numParams, values);
        } catch (IllegalStateException ex) {
            getLogger().severe("Could not call internal database select: Paramaters mixmatch (SQL: " + SQL + ")");
            ex.printStackTrace();
        } catch (SQLException ex) {
            getLogger().severe("Could not call internal database select: SQL error (SQL: " + SQL + ")");
            ex.printStackTrace();
        }
        return null;
    }
}
