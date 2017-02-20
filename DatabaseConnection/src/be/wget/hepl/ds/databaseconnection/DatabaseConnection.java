/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.databaseconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William Gathoye <wget>
 */
public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private static Connection connection;
    
    // Set the class as protected to defeat instantiation.
    protected DatabaseConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
          
            System.out.println("Driver JDBC-OBDC for maria db loaded");

            connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/hepl_distributed_systems?user=root");
            
            System.out.println("Connection to database established");
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
    
    public static Connection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return connection;
    }

}
