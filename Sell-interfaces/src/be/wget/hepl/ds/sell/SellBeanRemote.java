/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.sell;

import be.wget.hepl.ds.dataobjects.Item;
import java.util.ArrayList;
import javax.ejb.Remote;

/**
 *
 * @author William Gathoye <wget>
 */
@Remote
public interface SellBeanRemote {

    ArrayList<Item> getAvailableItems();
    void makeABid(int batchId, float amount);
    
}
