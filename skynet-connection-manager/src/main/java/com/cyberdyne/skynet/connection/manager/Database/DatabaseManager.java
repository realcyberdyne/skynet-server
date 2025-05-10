package com.cyberdyne.skynet.connection.manager.Database;

import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final String DATABASE_ADDRESS = "db.db";
    private final String DATABASE_CONNECTION_ADDRESS = "jdbc:sqlite:" + DATABASE_ADDRESS;
    private Connection dbConnection;

    /**
     * Constructor for DatabaseManager
     */
    public DatabaseManager() {
        try {
            // Check if database exists
            File database = new File(DATABASE_ADDRESS);
            boolean dbExists = checkDatabaseExists(database);

            // Create database connection
            createConnection();

            // If database didn't exist, create tables
            if (!dbExists)
            {
                createTablesInDB();
            }
        } catch (Exception e) {
            System.out.println("Error in DatabaseManager initialization: " + e.getMessage());
        }
    }

    /**
     * Check if the SQLite database file exists
     * @param databaseFile File object for the database
     * @return true if database exists, false otherwise
     */
    private boolean checkDatabaseExists(File databaseFile) {
        return databaseFile.exists();
    }

    /**
     * Create a connection to the database
     * @return true if connection was successful, false otherwise
     */
    private boolean createConnection() {
        try {
            dbConnection = DriverManager.getConnection(DATABASE_CONNECTION_ADDRESS);
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating database connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create tables in the database
     * @return true if tables were created successfully, false otherwise
     */
    public boolean createTablesInDB() {
        try {
            Statement statement = dbConnection.createStatement();

            // Create connection_models table
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS connection_tbl (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "key TEXT NOT NULL, " +
                            "protocol TEXT NOT NULL, " +
                            "create_user_id INTEGER NOT NULL, " +
                            "status INTEGER DEFAULT 0)"
            );

            // Create users_model table
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users_tbl (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "datetime TEXT NOT NULL)"
            );

            statement.close();

            //Get add admin user to database
            Users_Model NUser=new Users_Model("admin","admin","");
            new UsersDTO().GetInsertNewUser(NUser);

            return true;
        } catch (SQLException e) {
            System.out.println("Error in creating database tables: " + e.getMessage());
            return false;
        }
    }


    /**
     * Get oration on database
     * @return oration status
     */
    public boolean OprationOnDatabase(String OprationQuary)
    {
        try {
            Statement statement = dbConnection.createStatement();

            // Create connection_models table
            statement.executeUpdate(OprationQuary);

            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error in creating database tables: " + e.getMessage());
            return false;
        }
    }



    /**
     * Get select oration on database
     * @return List
     */
    public ArrayList<ArrayList<String>> SelectFromDatabase(String SelectQuary)
    {
        ArrayList<ArrayList<String>> Result=new ArrayList<>();

        try
        {
            Statement statement = dbConnection.createStatement();

            //Get result set
            ResultSet Rs=statement.executeQuery(SelectQuary);
            ResultSetMetaData RSMD=Rs.getMetaData();
            int colnumcount = RSMD.getColumnCount();

            while (Rs.next())
            {
                ArrayList<String> SubResult=new ArrayList<>();
                for (int i=1;i<=colnumcount;i++)
                {
                    Object value = Rs.getObject(i);

                    if (value == null)
                    {
                        SubResult.add("null");
                    }
                    else
                    {
                        SubResult.add(value.toString());
                    }
                }
                Result.add(SubResult);
            }

            statement.close();
            return Result;
        }
        catch (SQLException e)
        {
            System.out.println("Error in select in database: " + e.getMessage());
            return Result;
        }
    }



    /**
     * Get the database connection
     * @return Current database connection
     */
    public Connection getConnection() {
        return dbConnection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}