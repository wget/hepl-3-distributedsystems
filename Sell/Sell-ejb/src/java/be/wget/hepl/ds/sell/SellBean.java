/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.sell;

import be.wget.hepl.ds.databaseconnection.DatabaseConnection;
import be.wget.hepl.ds.dataobjects.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author William Gathoye <wget>
 */
@Stateless
public class SellBean implements SellBeanRemote {
    
    private Connection db;

    @Override
    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> itemsList = new ArrayList<>();
        Item item = new Item();
        try {
            
            db = DatabaseConnection.getInstance();
            if (db == null) {
                System.out.println("EJBSell: Database connection issue");
            }
        
            PreparedStatement statement =
                db.prepareStatement("select * from item");
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                System.out.println("EJBSell: No item found in the database");
            }
            
            do {
                item.setId(rs.getInt("id"));
                item.setDescription(rs.getString("description"));
                item.setAnnouncedPrice(rs.getFloat("announcedPrice"));
                item.setSellPrice(rs.getFloat("sellPrice"));
                item.setSellDate(rs.getDate("sellDate"));
                item.setBuyerId(rs.getInt("buyerId"));
                item.setBatchId(rs.getInt("batchId"));
                
                itemsList.add(item);
                
            } while (rs.next());

        } catch (SQLException ex) {
            Logger.getLogger(SellBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return itemsList;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
