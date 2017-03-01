/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.dataobjects;

import java.io.Serializable;

/**
 *
 * @author William Gathoye <wget>
 */
public class BidUserMessage implements Serializable {
    private Bid bid;
    private Item item;
    private Seller seller;
    private Buyer buyer;
    public static enum AnswerType {
        ITEM_DOES_NOT_EXIST,
        ITEM_ALREADY_BOUGHT,
        SELLER_DOES_NOT_EXIST,
        BID_WON,
        BID_WON_DB_ISSUE,
        BID_LOST,
        BID_150_PERCENT_ACCEPTED,
        BID_LESS_THAN_CURRENT_BEST,
        BID_GREATER_THAN_CURRENT_BEST_QUESTION,
        BID_GREATER_THAN_CURRENT_BEST_ANSWER
    }
    private AnswerType bidUserMessageType;
    private String bidUserMessageDestination;

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public AnswerType getBidUserMessageType() {
        return bidUserMessageType;
    }

    public void setBidUserMessageType(AnswerType bidUserMessageType) {
        this.bidUserMessageType = bidUserMessageType;
    }

    public String getBidUserMessageDestination() {
        return bidUserMessageDestination;
    }

    public void setBidUserMessageDestination(String bidUserMessageDestination) {
        this.bidUserMessageDestination = bidUserMessageDestination;
    }


}
