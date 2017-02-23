/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.dataobjects;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author William Gathoye <wget>
 */
public class Log implements Serializable {
    private int id;
    private String info;
    // We must use a timestamp and not data for precision
    // src.: http://stackoverflow.com/a/2306051/3514658
    private java.sql.Timestamp timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
