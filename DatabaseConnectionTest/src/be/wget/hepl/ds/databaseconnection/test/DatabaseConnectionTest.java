/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.databaseconnection.test;

import be.wget.hepl.ds.dataobjects.Item;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William Gathoye <wget>
 */
public class DatabaseConnectionTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
          
            System.out.println("Driver JDBC-OBDC for maria db loaded");

        
            Connection connection = connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/hepl_distributed_systems?user=root");

            System.out.println("Connection to database established");
            
            if (connection == null) {
                System.out.println("EJBSell: Database connection issue");
                return;
            }
        
            PreparedStatement statement =
                connection.prepareStatement("select * from item");
            ResultSet rs = statement.executeQuery();
            
            if (!rs.next()) {
                System.out.println("EJBSell: No item found in the database");
                return;
            }
            
            ArrayList<Item> itemsList = new ArrayList<>();
            Item item = new Item();
            
            do {
                item.setId(rs.getInt("id"));
                item.setDescription(rs.getString("description"));
                item.setAnnouncedPrice(rs.getFloat("announcedPrice"));
                item.setSellPrice(rs.getFloat("sellPrice"));
                item.setSellDate(rs.getDate("sellDate"));
                item.setBuyerId(rs.getInt("buyerId"));
                item.setBatchId(rs.getInt("batchId"));
                
                itemsList.add(item);
                
                System.out.println("DEBUG: " + item.getDescription());
                
            } while (rs.next());
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
