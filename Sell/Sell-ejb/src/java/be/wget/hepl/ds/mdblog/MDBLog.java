/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.mdblog;

import be.wget.hepl.ds.databaseconnection.DatabaseConnection;
import be.wget.hepl.ds.dataobjects.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 *
 * @author William Gathoye <wget>
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "jms/SellTopic"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/SellTopic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "jms/SellTopic"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "destination LIKE 'mdblog'")
})
public class MDBLog implements MessageListener {
    
    private Connection db;
    
    public MDBLog() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage)message;
            Log log = (Log)msg.getObject();
            
            db = DatabaseConnection.getInstance();
            if (db == null) {
                System.out.println("MDBLog: Database connection issue");
            }

            PreparedStatement statement = db.prepareStatement(
                    "insert into log(timestamp, info) values (?,?)");
            statement.setTimestamp(1, log.getTimestamp());
            statement.setString(2, log.getInfo());
            statement.executeUpdate();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(log.getTimestamp().getTime());
            System.out.println(
                "MDBLog: " + cal +
                "[" + log.getId() +
                "] " + log.getInfo());
            
        } catch (JMSException ex) {
            Logger.getLogger(MDBLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(MDBLog.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
}
