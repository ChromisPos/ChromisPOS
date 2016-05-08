//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) (c) 2015-2016
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
import java.util.HashMap;
import java.util.Map;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.payment.ConfigPaymentPanelBluePay20POST;
import uk.chromis.pos.payment.ConfigPaymentPanelCaixa;
import uk.chromis.pos.payment.ConfigPaymentPanelEmpty;
import uk.chromis.pos.payment.ConfigPaymentPanelGeneric;
import uk.chromis.pos.payment.ConfigPaymentPanelLinkPoint;
import uk.chromis.pos.payment.PaymentConfiguration;

/**
 *
 * @author adrianromero
 * @author Mikel Irurita
 */
public class JPanelConfigPayment extends javax.swing.JPanel implements PanelConfig {

    private final DirtyManager dirty = new DirtyManager();
    private final Map<String, PaymentConfiguration> paymentsName = new HashMap<>();
    private PaymentConfiguration pc;
    
    /** Creates new form JPanelConfigPayment */
    public JPanelConfigPayment() {
        
        initComponents();
                
        // dirty manager
        jcboCardReader.addActionListener(dirty);
        jcboPaymentGateway.addActionListener(dirty);
        jchkPaymentTest.addActionListener(dirty);
        jchkPaymentAcceptBank.addActionListener(dirty);
        jchkPaymentAcceptCash.addActionListener(dirty);
        jchkPaymentAcceptAccount.addActionListener(dirty);
        jchkPaymentAcceptCheque.addActionListener(dirty);
        jchkPaymentAcceptVoucher.addActionListener(dirty);
        jchkPaymentAcceptCard.addActionListener(dirty);
        jchkPaymentAcceptFree.addActionListener(dirty);
        
        // Payment Provider                
        initPayments("Not defined", new ConfigPaymentPanelEmpty());
        initPayments("external", new ConfigPaymentPanelEmpty());
        initPayments("PayPoint / SecPay", new ConfigPaymentPanelGeneric());
        initPayments("AuthorizeNet", new ConfigPaymentPanelGeneric());
        initPayments("BluePay AUTH.NET EMU", new ConfigPaymentPanelBluePay20POST()); 
        initPayments("BluePay 2.0 POST", new ConfigPaymentPanelBluePay20POST()); 
        initPayments("Planetauthorize", new ConfigPaymentPanelGeneric());
        initPayments("First Data / LinkPoint / YourPay", new ConfigPaymentPanelLinkPoint());
        initPayments("PaymentsGateway.net", new ConfigPaymentPanelGeneric());
        initPayments("La Caixa (Spain)", new ConfigPaymentPanelCaixa());
        
        // Lector de tarjetas.
        jcboCardReader.addItem("Not defined");
        jcboCardReader.addItem("Generic");
        jcboCardReader.addItem("Intelligent");
        jcboCardReader.addItem("Keyboard");
        
        
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

        jcboCardReader.setSelectedItem(AppConfig.getInstance().getProperty("payment.magcardreader"));
        jcboPaymentGateway.setSelectedItem(AppConfig.getInstance().getProperty("payment.gateway"));
        jchkPaymentTest.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.testmode")));       
        
        jchkPaymentAcceptBank.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptbank")));
        jchkPaymentAcceptCash.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptcash")));
        jchkPaymentAcceptAccount.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptaccount")));
        jchkPaymentAcceptCheque.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptcheque")));
        jchkPaymentAcceptVoucher.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptvoucher")));
        jchkPaymentAcceptCard.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptcard")));
        jchkPaymentAcceptFree.setSelected(Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.acceptfree")));
        
        pc.loadProperties();
        dirty.setDirty(false);
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void saveProperties() {
        
        AppConfig.getInstance().setProperty("payment.magcardreader", comboValue(jcboCardReader.getSelectedItem()));
        AppConfig.getInstance().setProperty("payment.gateway", comboValue(jcboPaymentGateway.getSelectedItem()));
        AppConfig.getInstance().setProperty("payment.testmode", Boolean.toString(jchkPaymentTest.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptcash", Boolean.toString(jchkPaymentAcceptCash.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptaccount", Boolean.toString(jchkPaymentAcceptAccount.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptcheque", Boolean.toString(jchkPaymentAcceptCheque.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptvoucher", Boolean.toString(jchkPaymentAcceptVoucher.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptcard", Boolean.toString(jchkPaymentAcceptCard.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptfree", Boolean.toString(jchkPaymentAcceptFree.isSelected()));
        AppConfig.getInstance().setProperty("payment.acceptbank", Boolean.toString(jchkPaymentAcceptBank.isSelected()));
        pc.saveProperties();
        dirty.setDirty(false);
    }
    
    private void initPayments(String name, PaymentConfiguration pc) {
        jcboPaymentGateway.addItem(name);
        paymentsName.put(name, pc);
    }
     
    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jcboPaymentGateway = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jcboCardReader = new javax.swing.JComboBox();
        jchkPaymentTest = new eu.hansolo.custom.SteelCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jchkPaymentAcceptBank = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptCash = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptAccount = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptCheque = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptVoucher = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptCard = new eu.hansolo.custom.SteelCheckBox();
        jchkPaymentAcceptFree = new eu.hansolo.custom.SteelCheckBox();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 450));

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(500, 200));
        jPanel2.setLayout(new java.awt.GridLayout(1, 1));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.paymentgateway")); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel13.setPreferredSize(new java.awt.Dimension(100, 30));

        jcboPaymentGateway.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboPaymentGateway.setPreferredSize(new java.awt.Dimension(200, 30));
        jcboPaymentGateway.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboPaymentGatewayActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.magcardreader")); // NOI18N
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 30));

        jcboCardReader.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboCardReader.setPreferredSize(new java.awt.Dimension(200, 30));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jchkPaymentTest.setText(bundle.getString("label.paymenttestmode")); // NOI18N

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jchkPaymentAcceptBank.setText(bundle.getString("tab.bank")); // NOI18N
        jchkPaymentAcceptBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptBankActionPerformed(evt);
            }
        });

        jchkPaymentAcceptCash.setText(bundle.getString("tab.cash")); // NOI18N
        jchkPaymentAcceptCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptCashActionPerformed(evt);
            }
        });

        jchkPaymentAcceptAccount.setText(bundle.getString("tab.debt")); // NOI18N
        jchkPaymentAcceptAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptAccountActionPerformed(evt);
            }
        });

        jchkPaymentAcceptCheque.setText(bundle.getString("tab.cheque")); // NOI18N
        jchkPaymentAcceptCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptChequeActionPerformed(evt);
            }
        });

        jchkPaymentAcceptVoucher.setText(bundle.getString("tab.paper")); // NOI18N
        jchkPaymentAcceptVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptVoucherActionPerformed(evt);
            }
        });

        jchkPaymentAcceptCard.setText(bundle.getString("tab.magcard")); // NOI18N
        jchkPaymentAcceptCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptCardActionPerformed(evt);
            }
        });

        jchkPaymentAcceptFree.setText(bundle.getString("tab.free")); // NOI18N
        jchkPaymentAcceptFree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkPaymentAcceptFreeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jchkPaymentAcceptBank, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchkPaymentAcceptCash, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jchkPaymentAcceptAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jchkPaymentAcceptVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jchkPaymentAcceptCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jchkPaymentAcceptFree, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jchkPaymentAcceptCard, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkPaymentAcceptBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkPaymentAcceptCash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkPaymentAcceptCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkPaymentAcceptAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkPaymentAcceptVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkPaymentAcceptFree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jchkPaymentAcceptCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jcboCardReader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jcboPaymentGateway, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap(176, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jchkPaymentTest, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(77, Short.MAX_VALUE))))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboCardReader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboPaymentGateway, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jchkPaymentTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jcboPaymentGatewayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboPaymentGatewayActionPerformed
    pc = paymentsName.get(comboValue(jcboPaymentGateway.getSelectedItem()));

    if (pc != null) {
        jPanel2.removeAll();
        jPanel2.add(pc.getComponent());
        jPanel2.revalidate();
        jPanel2.repaint(); 
    }
}//GEN-LAST:event_jcboPaymentGatewayActionPerformed

    private void jchkPaymentAcceptBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptBankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptBankActionPerformed

    private void jchkPaymentAcceptCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptCashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptCashActionPerformed

    private void jchkPaymentAcceptAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptAccountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptAccountActionPerformed

    private void jchkPaymentAcceptChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptChequeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptChequeActionPerformed

    private void jchkPaymentAcceptVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptVoucherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptVoucherActionPerformed

    private void jchkPaymentAcceptCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptCardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptCardActionPerformed

    private void jchkPaymentAcceptFreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkPaymentAcceptFreeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkPaymentAcceptFreeActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox jcboCardReader;
    private javax.swing.JComboBox jcboPaymentGateway;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptAccount;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptBank;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptCard;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptCash;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptCheque;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptFree;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentAcceptVoucher;
    private eu.hansolo.custom.SteelCheckBox jchkPaymentTest;
    // End of variables declaration//GEN-END:variables
    
}
