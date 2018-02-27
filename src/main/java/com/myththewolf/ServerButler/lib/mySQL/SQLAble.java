package com.myththewolf.ServerButler.lib.mySQL;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.logging.Loggable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLAble extends Loggable {

    default Connection getSQLConnection() {
        return ServerButler.connector.getConnection();
    }

    default void prepareAndExecuteUpdateThrow(String SQL, int numParams, Object... values) throws IllegalStateException, SQLException {
        if (numParams != values.length) {
            throw new IllegalStateException("Num of params do not match values (SQL: " + SQL + ")");
        }
        PreparedStatement preparedStatement = getSQLConnection().prepareStatement(SQL);
        for (int i = 0; i < numParams; i++) {
            Object singleValue = values[i];
            if (singleValue instanceof String) {
                preparedStatement.setString(i + 1, ((String) singleValue));
                continue;
            } else if (singleValue instanceof Integer) {
                preparedStatement.setInt(i + 1, ((Integer) singleValue));
                continue;
            } else if (singleValue instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, ((Boolean) singleValue));
                continue;
            } else if (singleValue != null) {
                preparedStatement.setString(i + 1, singleValue.toString());
                continue;
            } else {
                preparedStatement.setString(i + 1, null);
                continue;
            }
        }
        getLogger().info(preparedStatement.toString());
        preparedStatement.executeUpdate();
    }

    default void prepareAndExecuteUpdateExceptionally(String SQL, int numParams, Object... values) {
        try {
            prepareAndExecuteUpdateThrow(SQL, numParams, values);
        } catch (IllegalStateException ex) {
            getLogger().severe("Could not call internal database update: Paramaters mixmatch (SQL: " + SQL + ")");
            ex.printStackTrace();
            return;
        } catch (SQLException ex) {
            getLogger().severe("Could not call internal database update: SQL error (SQL: " + SQL + ")");
            ex.printStackTrace();
            return;
        }
    }

    default ResultSet prepareAndExecuteSelectThrow(String SQL, int numParams, Object... values) throws IllegalStateException, SQLException {
        if (numParams != values.length) {
            throw new IllegalStateException("Num of params do not match values (SQL: " + SQL + ")");
        }
        PreparedStatement preparedStatement = getSQLConnection().prepareStatement(SQL);
        for (int i = 0; i < numParams; i++) {
            Object singleValue = values[i];
            if (singleValue instanceof String) {
                preparedStatement.setString(i + 1, ((String) singleValue));
                continue;
            } else if (singleValue instanceof Integer) {
                preparedStatement.setInt(i + 1, ((Integer) singleValue));
                continue;
            } else if (singleValue instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, ((Boolean) singleValue));
                continue;
            } else {
                preparedStatement.setString(i + 1, singleValue.toString());
                continue;
            }
        }
        return preparedStatement.executeQuery();
    }

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
