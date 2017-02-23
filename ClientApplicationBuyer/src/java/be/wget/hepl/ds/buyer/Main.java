/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.wget.hepl.ds.buyer;

import be.wget.hepl.ds.dataobjects.Item;
import be.wget.hepl.ds.sell.SellBeanRemote;
import java.util.ArrayList;
import javax.ejb.EJB;

/**
 *
 * @author William Gathoye <wget>
 */
public class Main {

    @EJB
    private static SellBeanRemote SellBean;

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Item> itemsList = SellBean.getAvailableItems();
        System.out.println(itemsList.size());
        
        for (int i = 0; i < itemsList.size(); i++) {
            System.out.println(((Item)itemsList.get(0)).getDescription());
        }
        
        
        SellBean.makeABid(1000, 250.65f);

    }
    
}
