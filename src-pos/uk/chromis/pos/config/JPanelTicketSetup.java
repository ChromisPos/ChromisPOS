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
package uk.chromis.pos.config;

import java.awt.Component;
import javax.swing.SpinnerNumberModel;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.forms.AppConfig;

/**
 *
 *
 */
public class JPanelTicketSetup extends javax.swing.JPanel implements PanelConfig {

    private DirtyManager dirty = new DirtyManager();
    private String receipt = "1";
    private Integer x = 0;
    private String receiptSize;
    private String pickupSize;
    private Integer ps = 0;

    /**
     *
     */
    public JPanelTicketSetup() {

        initComponents();

        jReceiptSize.addChangeListener(dirty);
        jPickupSize.addChangeListener(dirty);
        jTextReceiptPrefix.getDocument().addDocumentListener(dirty);
        m_jReceiptPrintOff.addActionListener(dirty);
        jchkSCOnOff.addActionListener(dirty);
        jchkSCRestaurant.addActionListener(dirty);
        jTextSCRate.getDocument().addDocumentListener(dirty);
        jLayawayId.addActionListener(dirty);
        jCreateOnOrderOnly.addActionListener(dirty);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }

    /**
     *
     * @return
     */
    @Override
    public Component getConfigComponent() {
        return this;
    }

    /**
     *
     * @param config
     */
    @Override
    public void loadProperties() {

        receiptSize = (AppConfig.getInstance().getProperty("till.receiptsize"));
        if (receiptSize == null || "".equals(receiptSize)) {
            jReceiptSize.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        } else {
            jReceiptSize.setModel(new SpinnerNumberModel(Integer.parseInt(receiptSize), 1, 20, 1));
        }

        pickupSize = (AppConfig.getInstance().getProperty("till.pickupsize"));
        if (pickupSize == null || "".equals(pickupSize)) {
            jPickupSize.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        } else {
            jPickupSize.setModel(new SpinnerNumberModel(Integer.parseInt(pickupSize), 1, 20, 1));
        }

        jTextReceiptPrefix.setText(AppConfig.getInstance().getProperty("till.receiptprefix"));
        receipt = "";
        x = 1;
        while (x < (Integer) jReceiptSize.getValue()) {
            receipt += "0";
            x++;
        }

        receipt += "1";
        jTicketExample.setText(jTextReceiptPrefix.getText() + receipt);
        m_jReceiptPrintOff.setSelected(AppConfig.getInstance().getBoolean("till.receiptprintoff"));

        String SCCheck = (AppConfig.getInstance().getProperty("till.SCRate"));
        if (SCCheck == null || SCCheck.equals("")) {
            AppConfig.getInstance().setProperty("till.SCRate", "10");
            jTextSCRate.setText("10");
        } else {
            jTextSCRate.setText(AppConfig.getInstance().getProperty("till.SCRate").toString());
        }
        // jTextSCRate.setText(AppConfig.getInstance().getProperty("till.SCRate").toString());

        jchkSCOnOff.setSelected(AppConfig.getInstance().getBoolean("till.SCOnOff"));
        jchkSCRestaurant.setSelected(AppConfig.getInstance().getBoolean("till.SCRestaurant"));

        jLayawayId.setSelected(AppConfig.getInstance().getBoolean("till.usepickupforlayaway"));
        jCreateOnOrderOnly.setSelected(AppConfig.getInstance().getBoolean("till.createorder"));

        jchkSCOnOffActionPerformed(null);

        dirty.setDirty(false);
    }

    /**
     *
     * @param config
     */
    @Override
    public void saveProperties() {
        AppConfig.getInstance().setProperty("till.receiptprefix", jTextReceiptPrefix.getText());
        AppConfig.getInstance().setProperty("till.receiptsize", jReceiptSize.getValue().toString());
        AppConfig.getInstance().setProperty("till.pickupsize", jPickupSize.getValue().toString());
        AppConfig.getInstance().setBoolean("till.receiptprintoff", m_jReceiptPrintOff.isSelected());
        AppConfig.getInstance().setBoolean("till.SCOnOff", jchkSCOnOff.isSelected());
        AppConfig.getInstance().setProperty("till.SCRate", jTextSCRate.getText());
        AppConfig.getInstance().setBoolean("till.SCRestaurant", jchkSCRestaurant.isSelected());
        AppConfig.getInstance().setBoolean("till.usepickupforlayaway", jLayawayId.isSelected());
        AppConfig.getInstance().setBoolean("till.createorder", jCreateOnOrderOnly.isSelected());

        dirty.setDirty(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField2 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jReceiptSize = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jTextReceiptPrefix = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTicketExample = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPickupSize = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        m_jReceiptPrintOff = new eu.hansolo.custom.SteelCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jTextSCRate = new javax.swing.JTextField();
        jLabelSCRate = new javax.swing.JLabel();
        jLabelSCRatePerCent = new javax.swing.JLabel();
        jchkSCOnOff = new eu.hansolo.custom.SteelCheckBox();
        jchkSCRestaurant = new eu.hansolo.custom.SteelCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLayawayId = new eu.hansolo.custom.SteelCheckBox();
        jCreateOnOrderOnly = new eu.hansolo.custom.SteelCheckBox();

        jTextField2.setText("jTextField2");

        setPreferredSize(new java.awt.Dimension(700, 500));
        setLayout(null);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("label.configreceipt"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.setLayout(null);

        jReceiptSize.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jReceiptSize.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        jReceiptSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jReceiptSizeStateChanged(evt);
            }
        });
        jPanel1.add(jReceiptSize);
        jReceiptSize.setBounds(190, 20, 50, 40);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(bundle.getString("Label.ticketsetupnumber")); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(20, 20, 160, 40);

        jTextReceiptPrefix.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTextReceiptPrefix.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextReceiptPrefix.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextReceiptPrefixKeyReleased(evt);
            }
        });
        jPanel1.add(jTextReceiptPrefix);
        jTextReceiptPrefix.setBounds(380, 20, 80, 40);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(bundle.getString("Label.ticketsetupprefix")); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(250, 20, 130, 40);

        jTicketExample.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTicketExample.setText("1");
        jTicketExample.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTicketExample.setEnabled(false);
        jPanel1.add(jTicketExample);
        jTicketExample.setBounds(510, 20, 170, 40);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("label.pickupcodesize")); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(20, 70, 160, 40);

        jPickupSize.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jPickupSize.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        jPickupSize.setToolTipText("");
        jPanel1.add(jPickupSize);
        jPickupSize.setBounds(190, 70, 50, 40);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        m_jReceiptPrintOff.setBorder(null);
        m_jReceiptPrintOff.setText(bundle.getString("label.receiptprint")); // NOI18N
        jPanel6.add(m_jReceiptPrintOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, -1));

        jPanel1.add(jPanel6);
        jPanel6.setBounds(20, 120, 540, 30);

        add(jPanel1);
        jPanel1.setBounds(10, 10, 730, 170);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("label.SChargepanel"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel5.setLayout(null);

        jTextSCRate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextSCRate.setText("10");
        jTextSCRate.setMaximumSize(new java.awt.Dimension(0, 25));
        jTextSCRate.setMinimumSize(new java.awt.Dimension(0, 0));
        jTextSCRate.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel5.add(jTextSCRate);
        jTextSCRate.setBounds(220, 50, 50, 30);

        jLabelSCRate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelSCRate.setText(bundle.getString("label.SCRate")); // NOI18N
        jLabelSCRate.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelSCRate.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelSCRate.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel5.add(jLabelSCRate);
        jLabelSCRate.setBounds(50, 50, 150, 30);

        jLabelSCRatePerCent.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelSCRatePerCent.setText(bundle.getString("label.SCZero")); // NOI18N
        jLabelSCRatePerCent.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelSCRatePerCent.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelSCRatePerCent.setPreferredSize(new java.awt.Dimension(0, 25));
        jPanel5.add(jLabelSCRatePerCent);
        jLabelSCRatePerCent.setBounds(280, 50, 20, 30);

        jchkSCOnOff.setText(bundle.getString("label.SCOnOff")); // NOI18N
        jchkSCOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkSCOnOffActionPerformed(evt);
            }
        });
        jPanel5.add(jchkSCOnOff);
        jchkSCOnOff.setBounds(20, 20, 200, 30);

        jchkSCRestaurant.setText(bundle.getString("label.SCRestaurant")); // NOI18N
        jPanel5.add(jchkSCRestaurant);
        jchkSCRestaurant.setBounds(320, 50, 180, 30);

        add(jPanel5);
        jPanel5.setBounds(10, 190, 730, 90);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Layaway details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLayawayId.setText(bundle.getString("label.layaway")); // NOI18N
        jPanel2.add(jLayawayId, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 300, -1));

        jCreateOnOrderOnly.setText(bundle.getString("label.createonorder")); // NOI18N
        jPanel2.add(jCreateOnOrderOnly, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 340, -1));

        add(jPanel2);
        jPanel2.setBounds(10, 290, 730, 60);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextReceiptPrefixKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextReceiptPrefixKeyReleased

        jTicketExample.setText(jTextReceiptPrefix.getText() + receipt);
    }//GEN-LAST:event_jTextReceiptPrefixKeyReleased

    private void jReceiptSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jReceiptSizeStateChanged

        receipt = "";
        x = 1;
        while (x < (Integer) jReceiptSize.getValue()) {
            receipt += "0";
            x++;
        }
        receipt += "1";
        jTicketExample.setText(jTextReceiptPrefix.getText() + receipt);

    }//GEN-LAST:event_jReceiptSizeStateChanged

    private void jchkSCOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkSCOnOffActionPerformed

        if (jchkSCOnOff.isSelected()) {
            jchkSCRestaurant.setVisible(true);
            jLabelSCRate.setVisible(true);
            jTextSCRate.setVisible(true);
            jLabelSCRatePerCent.setVisible(true);
        } else {
            jchkSCRestaurant.setVisible(false);
            jLabelSCRate.setVisible(false);
            jTextSCRate.setVisible(false);
            jLabelSCRatePerCent.setVisible(false);
        }

    }//GEN-LAST:event_jchkSCOnOffActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private eu.hansolo.custom.SteelCheckBox jCreateOnOrderOnly;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelSCRate;
    private javax.swing.JLabel jLabelSCRatePerCent;
    private eu.hansolo.custom.SteelCheckBox jLayawayId;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSpinner jPickupSize;
    private javax.swing.JSpinner jReceiptSize;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextReceiptPrefix;
    private javax.swing.JTextField jTextSCRate;
    private javax.swing.JTextField jTicketExample;
    private eu.hansolo.custom.SteelCheckBox jchkSCOnOff;
    private eu.hansolo.custom.SteelCheckBox jchkSCRestaurant;
    private eu.hansolo.custom.SteelCheckBox m_jReceiptPrintOff;
    // End of variables declaration//GEN-END:variables

}
