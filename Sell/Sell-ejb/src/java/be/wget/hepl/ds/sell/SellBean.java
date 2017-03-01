/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.sell;

import be.wget.hepl.ds.databaseconnection.DatabaseConnection;
import be.wget.hepl.ds.dataobjects.Bid;
import be.wget.hepl.ds.dataobjects.Item;
import be.wget.hepl.ds.dataobjects.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;

/**
 *
 * @author William Gathoye <wget>
 */
@Stateless
public class SellBean implements SellBeanRemote {

    @Resource(mappedName = "jms/SellTopicFactory")
    private TopicConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/SellTopic")
    private Topic sellTopic;
    
    @Resource
    private SessionContext context;
    
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
        
        log(context.getCallerPrincipal().getName() +
            " asked the list of available items");
        
        return itemsList;
    }

    @Override
    public void makeABid(int itemId, float amount) {
        javax.jms.Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(sellTopic);
            connection.start();

            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty("destination", "mdbbid");

            Bid bid = new Bid();
            bid.setItemId(itemId);
            bid.setAmount(amount);

            String from = context.getCallerPrincipal().getName();
            bid.setFrom(from);

            message.setObject(bid);
            producer.send(message);

            log(from + " made a bid of " + amount + " for the batch " + itemId);

        } catch (JMSException ex) {
            Logger.getLogger(SellBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    Logger.getLogger(SellBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void log(String message) {
        javax.jms.Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(sellTopic);
            connection.start();

            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setStringProperty("destination", "mdblog");

            Log log = new Log();
            log.setTimestamp(new Timestamp((new java.util.Date()).getTime()));
            log.setInfo(message);

            objectMessage.setObject(log);
            producer.send(objectMessage);

        } catch (JMSException ex) {
            Logger.getLogger(SellBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    Logger.getLogger(SellBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
