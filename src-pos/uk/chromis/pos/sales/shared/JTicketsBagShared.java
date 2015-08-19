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
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.
package uk.chromis.pos.sales.shared;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.admin.DataLogicAdmin;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.sales.DataLogicReceipts;
import uk.chromis.pos.sales.JTicketsBag;
import uk.chromis.pos.sales.SharedTicketInfo;
import uk.chromis.pos.sales.TicketsEditor;
import uk.chromis.pos.ticket.TicketInfo;
import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 *   
 */
public class JTicketsBagShared extends JTicketsBag {

    private String m_sCurrentTicket = null;
    private DataLogicReceipts dlReceipts = null;
    private DataLogicAdmin dlAdmin;

    /**
     * Creates new form JTicketsBagShared
     *
     * @param app
     * @param panelticket
     */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);

        dlReceipts = (DataLogicReceipts) app.getBean("uk.chromis.pos.sales.DataLogicReceipts");
        dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        initComponents();
    }

    /**
     *
     */
    @Override
    public void activate() {

        // precondicion es que no tenemos ticket activado ni ticket en el panel
        m_sCurrentTicket = null;
        selectValidTicket();

        // Authorisation
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("uk.chromis.pos.sales.JPanelTicketEdits"));

    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        // precondicion es que tenemos ticket activado aqui y ticket en el panel 
        saveCurrentTicket();

        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);

        return true;

        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }

    /**
     *
     */
    @Override
    public void deleteTicket() {
        m_sCurrentTicket = null;
        selectValidTicket();
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return new JPanel();
    }

    private void saveCurrentTicket() {
// Thanks Ibastavd & sunnytang 2012/2013 - Mod JG 23 Jul 13    
// save current ticket, if exists,
        if (m_sCurrentTicket != null) {
            try {
                dlReceipts.insertSharedTicket(m_sCurrentTicket, m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
                TicketInfo l = dlReceipts.getSharedTicket(m_sCurrentTicket);
                if (l.getLinesCount() == 0) {
                    dlReceipts.deleteSharedTicket(m_sCurrentTicket);
                }
                checkLayaways();
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }

    private void setActiveTicket(String id) throws BasicException {
        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null) {
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            dlReceipts.getPickupId(id);
            Integer pickUp = dlReceipts.getPickupId(id);
            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            m_panelticket.setActiveTicket(ticket, null);
            ticket.setPickupId(pickUp);
        }
        checkLayaways();
        // END TRANSACTION                 
    }

    private void checkLayaways() {
        AppConfig m_config = new AppConfig(new File((System.getProperty("user.home")), AppLocal.APP_ID + ".properties"));
        m_config.load();
        List<SharedTicketInfo> nl;
        try {
            //delete.rightslevel          
            if ("true".equals(m_config.getProperty("sharedticket.currentuser"))) {
                nl = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
            } else {
                nl = dlReceipts.getSharedTicketList();
            }
     //       if (Integer.parseInt(dlAdmin.getRightsLevelByID(m_App.getAppUserView().getUser().getRole())) <= (Integer.parseInt(m_config.getProperty("delete.rightslevel")))) {
     //       } else {
     //           nl = dlReceipts.getSharedTicketList();
     //       }

            if (nl.isEmpty()) {
                m_jListTickets.setText("");
                m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_pending.png")));
            } else {
                m_jListTickets.setText("*");
                m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sales_active.png")));
            }
        } catch (BasicException e) {
        }
    }

    private void selectValidTicket() {
        AppConfig m_config = new AppConfig(new File((System.getProperty("user.home")), AppLocal.APP_ID + ".properties"));
        m_config.load();
        List<SharedTicketInfo> l;
        try {
            if ("true".equals(m_config.getProperty("sharedticket.currentuser"))) {
                l = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
            } else {
                l = dlReceipts.getSharedTicketList();
            }
      //      if (Integer.parseInt(dlAdmin.getRightsLevelByID(m_App.getAppUserView().getUser().getRole())) <= (Integer.parseInt(m_config.getProperty("delete.rightslevel")))) {
      //      } else {
      //          l = dlReceipts.getSharedTicketList();
      //      }
            checkLayaways();
            if (l.isEmpty()) {
                newTicket();
            } else {
                setActiveTicket(l.get(l.size() - 1).getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
            checkLayaways();
            newTicket();
        }
    }

    private void newTicket() {
        saveCurrentTicket();
        TicketInfo ticket = new TicketInfo();
        m_sCurrentTicket = UUID.randomUUID().toString(); // m_fmtid.format(ticket.getId());
        m_panelticket.setActiveTicket(ticket, null);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        m_jNewTicket = new javax.swing.JButton();
        m_jDelTicket = new javax.swing.JButton();
        m_jListTickets = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        m_jNewTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_new.png"))); // NOI18N
        m_jNewTicket.setToolTipText("New Sale");
        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jNewTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setPreferredSize(new java.awt.Dimension(50, 40));
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jNewTicket);

        m_jDelTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_delete.png"))); // NOI18N
        m_jDelTicket.setToolTipText("Cancel Sale");
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jDelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setPreferredSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });
        jPanel1.add(m_jDelTicket);

        m_jListTickets.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jListTickets.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_pending.png"))); // NOI18N
        m_jListTickets.setToolTipText("Layaways");
        m_jListTickets.setFocusPainted(false);
        m_jListTickets.setFocusable(false);
        m_jListTickets.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jListTickets.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jListTickets.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jListTickets.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jListTickets.setPreferredSize(new java.awt.Dimension(50, 40));
        m_jListTickets.setRequestFocusEnabled(false);
        m_jListTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListTicketsActionPerformed(evt);
            }
        });
        jPanel1.add(m_jListTickets);

        add(jPanel1, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jListTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListTicketsActionPerformed
        
                
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppConfig m_config = new AppConfig(new File((System.getProperty("user.home")), AppLocal.APP_ID + ".properties"));
                m_config.load();
                List<SharedTicketInfo> l;
                try {
                    if ("true".equals(m_config.getProperty("sharedticket.currentuser"))) {
                        l = dlReceipts.getSharedTicketListByUser(m_App.getAppUserView().getUser().getName());
                    } else {
                        l = dlReceipts.getSharedTicketList();
                    }
       //             if (Integer.parseInt(dlAdmin.getRightsLevelByID(m_App.getAppUserView().getUser().getRole())) <= (Integer.parseInt(m_config.getProperty("delete.rightslevel")))) {
       //             } else {
       //                 l = dlReceipts.getSharedTicketList();
       //             }
                    JTicketsBagSharedList listDialog = JTicketsBagSharedList.newJDialog(JTicketsBagShared.this);
                    String id = listDialog.showTicketsList(l);

                    if (id != null) {
                        saveCurrentTicket();
                        setActiveTicket(id);                        
                    }
                } catch (BasicException e) {
                    new MessageInf(e).show(JTicketsBagShared.this);                  
                        newTicket();
                }
            }
        });
        
    }//GEN-LAST:event_m_jListTicketsActionPerformed

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            deleteTicket();

        }

    }//GEN-LAST:event_m_jDelTicketActionPerformed

    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed
        newTicket();
    }//GEN-LAST:event_m_jNewTicketActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton m_jDelTicket;
    private javax.swing.JButton m_jListTickets;
    private javax.swing.JButton m_jNewTicket;
    // End of variables declaration//GEN-END:variables

}
