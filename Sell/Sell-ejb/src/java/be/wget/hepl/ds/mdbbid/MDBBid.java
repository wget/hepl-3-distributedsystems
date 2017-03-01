/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.mdbbid;

import be.wget.hepl.ds.databaseconnection.DatabaseConnection;
import be.wget.hepl.ds.dataobjects.Bid;
import be.wget.hepl.ds.dataobjects.BidUserMessage;
import be.wget.hepl.ds.dataobjects.Buyer;
import be.wget.hepl.ds.dataobjects.Item;
import be.wget.hepl.ds.dataobjects.Seller;
import be.wget.hepl.ds.sell.SellBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;

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
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "destination LIKE 'mdbbid'")
})
public class MDBBid implements MessageListener {

    @Resource(mappedName = "jms/SellTopicFactory")
    private TopicConnectionFactory connectionFactory;

    @Resource(mappedName = "jms/SellTopic")
    private Topic sellTopic;
        
    private Connection db;
    
    public MDBBid() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {  
            ObjectMessage msg = (ObjectMessage)message;
            Bid bid;

            bid = (Bid)msg.getObject();
            
            manageBid(bid);
            
            
        } catch (JMSException ex) {
            Logger.getLogger(MDBBid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void manageBid(Bid bid) {

        BidUserMessage bidUserMessage = new BidUserMessage();
        bidUserMessage.setBid(bid);

        Item item = getItemFromId(bid.getItemId());
        if (item == null) {
            // Warn buyer
            bidUserMessage.setBidUserMessageDestination(bid.getFrom());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.ITEM_DOES_NOT_EXIST);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: onMessage: " +
                "the item " + bid.getItemId() + " does not exist in db");
            return;
        }
        bidUserMessage.setItem(item);

        Seller seller = getSellerFromItem(item);
        if (seller == null) {
            // Warn buyer
            bidUserMessage.setBidUserMessageDestination(bid.getFrom());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.SELLER_DOES_NOT_EXIST);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: onMessage: " +
                "the seller for the item " + bid.getItemId() +
                " does not exist in db");
            return;
        }
        bidUserMessage.setSeller(seller);

        Buyer buyer = getBuyerFromLogin(bid.getFrom());
        if (buyer == null) {
            // Warn buyer
            bidUserMessage.setBidUserMessageDestination(bid.getFrom());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.SELLER_DOES_NOT_EXIST);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: onMessage: " +
                "unable to retrieve the complete infos of the buyer " +
                bid.getFrom());
            return;
        }
        bidUserMessage.setBuyer(buyer);

        if (item.getSellDate() != null) {
            bidUserMessage.setBidUserMessageDestination(buyer.getLogin());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.ITEM_ALREADY_BOUGHT);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: manageBid:" +
                "the item " + bid.getItemId() + " has been already bought");
            return;
        }

        if (bid.getAmount() >= (1.5 * item.getAnnouncedPrice())) {
            try {
                makeBidWinnerInDb(bid);
            } catch (SQLException ex) {
                Logger.getLogger(MDBBid.class.getName()).log(Level.SEVERE, null, ex);
                // Warn buyer issue DB
                bidUserMessage.setBidUserMessageDestination(buyer.getLogin());
                bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.BID_WON_DB_ISSUE);
                sendBidUserMessage(bidUserMessage);
            }

            // Warn seller a buyer has won
            bidUserMessage.setBidUserMessageDestination(seller.getLogin());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.BID_150_PERCENT_ACCEPTED);
            sendBidUserMessage(bidUserMessage);

            // Warn buyer he has won
            bidUserMessage.setBidUserMessageDestination(buyer.getLogin());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.BID_WON);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: manageBid:" +
                buyer.getLogin() + " has won the item " + bid.getItemId());
            return;     
        }

        if (bid.getAmount() < item.getSellPrice()) {
            // Warn buyer he hast lost
            bidUserMessage.setBidUserMessageDestination(buyer.getLogin());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.BID_LOST);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: manageBid:" +
                buyer.getLogin() + " has lost the item " + bid.getItemId());
            return;
        }

        if (bid.getAmount() > item.getSellPrice()) {
            // Ask seller if he wants to accept the buyer's offer
            bidUserMessage.setBidUserMessageDestination(seller.getLogin());
            bidUserMessage.setBidUserMessageType(
                BidUserMessage.AnswerType.BID_GREATER_THAN_CURRENT_BEST_QUESTION);
            sendBidUserMessage(bidUserMessage);

            System.out.println("MDBBid: manageBid: asking " +
                seller.getLogin() + " if he wants to accept offer from " +
                buyer.getLogin());
        }
    }
    
    private void makeBidWinnerInDb(Bid bid) throws SQLException {
        db = DatabaseConnection.getInstance();
        if (db == null) {
            throw new SQLException("makeBidWinner: Database connection issue");
        }

        PreparedStatement statement = db.prepareStatement(
            "update item set sellPrice = ?, sellDate = ? where id = ?");
        statement.setFloat(1, bid.getAmount());
        statement.setDate(2, (Date) Calendar.getInstance().getTime());
        statement.setInt(3, bid.getItemId());
        statement.executeUpdate();
    }
    
    private Buyer getBuyerFromLogin(String login) {
        try {
            db = DatabaseConnection.getInstance();
            if (db == null) {
                throw new SQLException(
                    "getBuyerFromLogin: Database connection issue");
            }
            
            PreparedStatement statement = db.prepareStatement(
                "select * from buyer where login like ?");
            statement.setString(1, login);
            
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new SQLException(
                    "getBuyerFromLogin: buyer " + login + " not found");
            }
            
            Buyer buyer = new Buyer();
            buyer.setId(rs.getInt("id"));
            buyer.setLastName(rs.getString("lastName"));
            buyer.setFirstName(rs.getString("firstName"));
            buyer.setLogin(login);
            
            return buyer;

        } catch (SQLException ex) {
            Logger.getLogger(MDBBid.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }          
    }
    
    private void sendBidUserMessage(BidUserMessage bidUserMessage) {
        javax.jms.Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(sellTopic);
            connection.start();
            
            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty(
                "destination",
                bidUserMessage.getBidUserMessageDestination());        
            message.setObject(bidUserMessage);
            producer.send(message);
        
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

    private Item getItemFromId(int itemId) {
        try {
            db = DatabaseConnection.getInstance();
            if (db == null) {
                throw new SQLException("getItem: Database connection issue");
            }
            
            PreparedStatement statement = db.prepareStatement(
                "select * from item where id = ?");
            statement.setInt(1, itemId);
            
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new SQLException("getItem: item " + itemId + " not found");
            }
            
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setDescription(rs.getString("description"));
            item.setAnnouncedPrice(rs.getFloat("announcedPrice"));
            item.setSellPrice(rs.getFloat("sellPrice"));
            item.setSellDate(rs.getDate("sellDate"));
            item.setBuyerId(rs.getInt("buyerId"));
            item.setBatchId(rs.getInt("batchId"));
            
            return item;

        } catch (SQLException ex) {
            Logger.getLogger(MDBBid.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }  
    }
    
    private Seller getSellerFromItem(Item item) {
        try {
            db = DatabaseConnection.getInstance();
            if (db == null) {
                throw new SQLException("getItem: Database connection issue");
            }
            
            PreparedStatement statement = db.prepareStatement(
                "select *"
                + "from seller,batch,item"
                + "where seller.id = batch.sellerId"
                + "and batch.id = item.batchId"
                + "and item.id  = ?");
            statement.setInt(1, item.getId());
            
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new SQLException(
                    "getSellerFromItem: Seller not found for item "
                    + item.getId());
            }
            
            Seller seller = new Seller();
            seller.setId(rs.getInt("id"));
            seller.setLastName(rs.getString("lastName"));
            seller.setFirstName(rs.getString("firstName"));
            seller.setLogin(rs.getString("login"));
           
            return seller;

        } catch (SQLException ex) {
            Logger.getLogger(MDBBid.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
