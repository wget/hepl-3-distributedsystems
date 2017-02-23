/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.sell.topicjms.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author William Gathoye <wget>
 */
public class Main {

    @Resource(mappedName = "jms/SellTopic")
    private static Topic sellTopic;

    @Resource(mappedName = "jms/SellTopicFactory")
    private static ConnectionFactory connectionFactory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            javax.jms.Connection connection;
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(sellTopic, "destination LIKE 'bidMDB'");
            connection.start();
            
            
            while (true) {
                TextMessage message = (TextMessage) consumer.receive();
                System.out.println("Destination : " + message.getStringProperty("destination"));
                System.out.println("BatchId     : " + message.getStringProperty("batchId"));
                System.out.println("Amount      : " + message.getFloatProperty("amount"));
            }
        
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    
}
