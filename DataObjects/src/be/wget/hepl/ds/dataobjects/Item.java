/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author William Gathoye <wget>
 */

public class Item implements Serializable {
    private int id;
    private String description;
    private float announcedPrice;
    private float sellPrice;
    private Date sellDate;
    private int buyerId;
    private int batchId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAnnouncedPrice() {
        return announcedPrice;
    }

    public void setAnnouncedPrice(float announcedPrice) {
        this.announcedPrice = announcedPrice;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }
    
}
