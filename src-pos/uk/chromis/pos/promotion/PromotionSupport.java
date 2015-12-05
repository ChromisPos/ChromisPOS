//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//     Chromis POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Chromis POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    

package uk.chromis.pos.promotion;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;

public class PromotionSupport {

    protected Session m_session;
    protected DataLogicSales m_salesLogic;
    protected DataLogicPromotions m_DataLogicPromotions;
    HashMap< String, PromotionInfo> m_promotionCache;
    private Component m_Parent;

    public PromotionSupport( Component parent,
                             DataLogicSales salesLogic,
                             DataLogicPromotions logicPromotions ) {
        m_Parent = parent;
        m_salesLogic = salesLogic;
        m_DataLogicPromotions = logicPromotions;
        m_promotionCache = new HashMap();
    }
    
     public void clearPromotionCache() {
        m_promotionCache.clear();
    } 
     
    private PromotionInfo getCachedPromotion( String id ) {
        
        PromotionInfo promotion = m_promotionCache.get(id);
        
        if( promotion == null ) {
            try {
                promotion = m_DataLogicPromotions.getPromotionInfo(id);
            } catch (BasicException ex) {
                Logger.getLogger(PromotionSupport.class.getName()).log(Level.SEVERE, null, ex);
            }
            if( promotion != null ) {
                m_promotionCache.put(id, promotion);
            }
        }
        
        return promotion;
    } 

    // Check to see if any promotion scripts need to run
    // Supported events: 
    //  Event                  
    //  promotion.addline  
    //  promotion.changeline 
    //  promotion.removeline
    //
    public Boolean checkPromotions( String event, 
            Boolean bwithActions,
            TicketInfo ticket,
            int selectedindex, 
            int effectedIndex ) throws ScriptException {
 
        if( ticket == null || event == null  )
            return false;
        
        Logger.getLogger( PromotionSupport.class.getName()).log(Level.INFO,
                "checkPromotions for event: {0}", event );

        HashMap< String, PromotionInfo> promotions  = new HashMap();
        
        for (TicketLineInfo line: ticket.getLines()) {
            String id = line.getPromotionId();
            if( id != null ) {
                // Only add if not already in the list
                if( promotions.get(id) == null ) {
                    // Promotion on this product so need to run the promotion script
                    PromotionInfo p = getCachedPromotion(id);
                    if( p != null && p.getIsEnabled() ) {
                        promotions.putIfAbsent(id, p );
                    }
                }
            }
        }
        
        if( !(promotions.isEmpty()) && bwithActions ) {
            // Promotion scripts need running
            for( String promotionID : promotions.keySet() ) {
                evalScript( promotions.get(promotionID), event,
                        ticket, selectedindex, effectedIndex );
            }
        }
        
        if( selectedindex >= ticket.getLines().size() ) {
            selectedindex = ticket.getLines().size();
        }
        return !(promotions.isEmpty());
    }
    
    public Object evalScript( PromotionInfo promotion, String event, 
              TicketInfo ticket, int selectedindex,
              int effectedIndex ) throws ScriptException {

        Logger.getLogger( PromotionSupport.class.getName()).log(Level.INFO, "Running script for: {0}", promotion.getName() );

        ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);

        script.put("ticket", ticket);
        script.put("dlsales", m_salesLogic);
        script.put("dlpromotions", m_DataLogicPromotions);
        script.put("support", this );
        script.put("promotion", promotion);
        script.put("event", event );
        script.put("selectedindex", selectedindex);
        script.put("effectedIndex", effectedIndex);
        
        return script.eval( promotion.getScript());
    }

    // Script support functions

    // Display a message as a popup dialog
    public void ShowMessage( String title, String message ) {

        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont=pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL",Font.PLAIN,20)));
        JLabel FontText = new JLabel(message);

        JOptionPane newpane = new JOptionPane( );
        newpane.setMessage(FontText);
        
        Dialog dlg = newpane.createDialog( title );
        dlg.setVisible( true );

        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(),originalFont.getStyle(),originalFont.getSize())));
    }

    // Count products in this promotion currently on the ticket
    public int CountProduct( PromotionInfo promotion,
            TicketInfo ticket,
            boolean bIncludePromotionAdded ) {
        int count = 0;

        for (TicketLineInfo line: ticket.getLines()) {
            if( bIncludePromotionAdded || line.isPromotionAdded() == false ) {
                String id = line.getPromotionId();
                if( id != null && id.contentEquals(promotion.getID()) )
                    ++count;
            }
        }
        return count;
    }

    // Discount all products in this ticket having the given promotion ID
    public void DiscountProducts( TicketInfo ticket,
        String promotionid,
        String sDiscountMessage, Double discountrate, Boolean bWithReset ) {

        if( bWithReset ) {
            for( int i = 0; i < ticket.getLines().size(); ++i) {
                TicketLineInfo line = ticket.getLine(i);
                String id = line.getPromotionId();
                if( id != null ) {
                    if( id.contentEquals(promotionid) ) {
                        RemoveDiscount( ticket, i );
                    }
                }
            }
        }
        
        for( int i = 0; i < ticket.getLines().size(); ++i) {
            TicketLineInfo line = ticket.getLine(i);
            String id = line.getPromotionId();
            if( id != null ) {
                if( id.contentEquals(promotionid) ) {
                    DiscountProduct( ticket, i, sDiscountMessage, discountrate );
                }
            }
        }
    }
    
    private static String sYes = new String("yes");
    
    // Discount a product
    public void DiscountProduct( TicketInfo ticket, int lineIndex,
            String sDiscountMessage, Double discountrate ) {

        TicketLineInfo productline = ticket.getLine(lineIndex);
        if( productline.isPromotionAdded() == false &&
            sYes.contentEquals(productline.getDiscounted() ) == false ) {
                
            double discount = Math.rint(productline.getPrice()*-1)
                                * (discountrate /100d);

            TicketLineInfo discountline = new TicketLineInfo(
                    productline.getProductID(),
                    sDiscountMessage,
                    productline.getProductTaxCategoryID(),
                    productline.getMultiply(),
                    discount,
                    productline.getTaxInfo()); 
            discountline.setPromotionAdded( true );
            discountline.setProperty( "product.promotionid", productline.getPromotionId());

            ticket.insertLine(lineIndex+1, discountline );
            productline.setDiscounted("yes");
        }
    }
    
    // Remove discount on a product
    // Pass in the index of the product, subsequent promotion discount lines
    // will be removed
    public void RemoveDiscount( TicketInfo ticket, int lineIndex ) {

        int index = lineIndex+1;
        TicketLineInfo productline = null;
        
        if( index < ticket.getLines().size() ) {
            productline = ticket.getLine( index );
        }
        
        while( productline != null && productline.isPromotionAdded()) {
            ticket.removeLine(index);
            if( index < ticket.getLines().size() ) {
                productline = ticket.getLine( index );
            } else {
                productline = null;
            }
        }
        
        ticket.getLine(lineIndex).setDiscounted("no");
    }
    
    // See if the indexed line is in the given promotion
    public Boolean isProductInPromotion( TicketInfo ticket, 
            int index, PromotionInfo promotion ) {
        
        TicketLineInfo productline = ticket.getLine( index );
        if( promotion.getID().contentEquals( productline.getPromotionId() ) ) {
            return true;
        }
        return false;
    }
            
    public void testcode( PromotionInfo promotion, String event, 
              TicketInfo ticket, int selectedindex,
              DataLogicSales dlsales,
              DataLogicPromotions dlpromotions,
              PromotionSupport support,
              int effectedIndex ) {

// promotion.percentoff
//
// Percentage off the price
//

// START OF USER EDITABLE VARIABLES

Double PERCENT_DISCOUNT = 10.0;

// END OF USER EDITABLE VARIABLES

//
//
// DO NOT EDIT THE CODE BELOW UNLESS YOU REALLY KNOW WHAT YOU ARE DOING
//      
   // Supported events: 
    //  Event                  
    //  promotion.addline    
    //  promotion.changeline   
    //  promotion.removeline   

// Called after a new line added to a ticket
if( new String("promotion.addline").contentEquals(event) ) {
    // Add discounts
    support.DiscountProducts( ticket, promotion.getID(), "  *" + promotion.getName(), PERCENT_DISCOUNT, false );
}

// Called after a line is deleted
// The ticket line and the promotion added lines are already deleted by the
// time we get here
if( new String("promotion.removeline").contentEquals(event) ) {
    // Do nothing
}

// Called when a ticket line is changed
if( new String("promotion.changeline").contentEquals(event) ) {
    if( support.isProductInPromotion( ticket, selectedindex, promotion ) ) {
        // Remove discounts and recalculate them
        support.DiscountProducts( ticket, promotion.getID(), "  *" + promotion.getName(), PERCENT_DISCOUNT, true );
    }
}
    }
}
