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

package uk.chromis.pos.payment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.RoundUtils;

/**
 *
 *   
 */
public class PaymentPanelBasic extends javax.swing.JPanel implements PaymentPanel {

    private double m_dTotal;
    private String m_sTransactionID;
    private JPaymentNotifier m_notifier;
    private Boolean m_Cashback;
    private Double m_CashbackPercent;
    private Double m_CashbackTransactionCharge;
    private Double m_CashbackTotalCharge;
    private Double m_CashbackLimit;
    private Double m_dTotalCashBack;
    private final Boolean priceWith00;
 
    /** Creates new form PaymentPanelSimple
     * @param notifier */
    public PaymentPanelBasic(JPaymentNotifier notifier) {
        
        m_dTotalCashBack = m_CashbackTotalCharge = 0.0;
        m_notifier = notifier;
        initComponents();
        
        m_jCashBackAmount.addPropertyChangeListener("Edition", new PaymentPanelBasic.RecalculateState());
        m_jCashBackAmount.addEditorKeys(m_jKeys);
        
        priceWith00 =("true".equals(AppConfig.getInstance().getProperty("till.pricewith00")));
        if (priceWith00) {
            // use '00' instead of '.'
            m_jKeys.dotIs00(true);
        }

    }
    
    private void printState() {

        Double value = m_jCashBackAmount.getDoubleValue();
        if (value == null || value == 0.0) {
            m_dTotalCashBack = 0.0;
        } else {
            
            if( m_CashbackLimit <= value ) {
                value = m_CashbackLimit;
                m_jCashBackAmount.setDoubleValue(value);
            }
            
            m_dTotalCashBack = value;
        }   

        if( m_dTotalCashBack == 0.0 ) {
            m_CashbackTotalCharge = 0.0;
        } else {
            m_CashbackTotalCharge = m_CashbackTransactionCharge +
                    ( m_dTotalCashBack * m_CashbackPercent )/100;
        }
        
        jLabelCharge.setText( Formats.CURRENCY.formatValue(m_CashbackTotalCharge) );
        jLabelPayment.setText( Formats.CURRENCY.formatValue(m_dTotal+m_dTotalCashBack+m_CashbackTotalCharge) );
        
    }
/**
     *
     * @return
     */
    @Override
    public JComponent getComponent(){
        return this;
    }

       private class RecalculateState implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }
    
    /**
     *
     * @param sTransaction
     * @param dTotal
     */
    @Override
    public void activate(String sTransaction, double dTotal) {
        
        m_sTransactionID = sTransaction;
        m_dTotal = dTotal;

        m_Cashback = Boolean.parseBoolean(AppConfig.getInstance().getProperty("payment.cashbackenabled") ) &&
                m_dTotal > 0.0;
        
        jLabel1.setVisible(!m_Cashback);
        jPanelCashBack.setVisible(m_Cashback);

        if( m_Cashback ) {
            m_jCashBackAmount.setDoubleValue(0.0);
            m_CashbackPercent = Double.parseDouble( AppConfig.getInstance().getProperty("payment.cashbackpercentcharge") );
            m_CashbackLimit = Double.parseDouble( AppConfig.getInstance().getProperty("payment.cashbacklimit") );
            m_CashbackTransactionCharge = Double.parseDouble( AppConfig.getInstance().getProperty("payment.cashbacktransactioncharge") );

            m_jCashBackAmount.reset();
            m_jCashBackAmount.activate();
        
        } else {
        
            jLabel1.setText(
                    m_dTotal > 0.0
                    ? AppLocal.getIntString("message.paymentgatewayext")
                    : AppLocal.getIntString("message.paymentgatewayextrefund"));
        }
        
        m_notifier.setStatus(true, true);            

        printState();
    }
    
    /**
     *
     * @return
     */
    @Override
    public PaymentInfoMagcard getPaymentInfoMagcard() {

        if (m_dTotal > 0.0) {
            return new PaymentInfoMagcard(
                    "",
                    "", 
                    "",
                    null,
                    null,
                    null,
                    m_sTransactionID,
                    m_dTotal,
                    m_CashbackTotalCharge,
                    m_dTotalCashBack );
        } else {
            return new PaymentInfoMagcardRefund( 
                    "",
                    "", 
                    "",
                    null,
                    null,
                    null,
                    m_sTransactionID,
                    m_dTotal );
        }
    } 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCashBack = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label1 = new java.awt.Label();
        m_jCashBackAmount = new uk.chromis.editor.JEditorCurrencyPositive();
        jLabelPayment = new javax.swing.JLabel();
        jLabelCharge = new javax.swing.JLabel();
        m_jKeys = new uk.chromis.editor.JEditorKeys();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel2.setText(bundle.getString("message.paymentgatewayextcashback")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText(bundle.getString("message.paymentgatewayextcashbackcharge")); // NOI18N

        label1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label1.setText(bundle.getString("message.paymentgatewayextcashbacktotal")); // NOI18N

        m_jCashBackAmount.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jLabelPayment.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabelPayment.setText("jLabel4");

        jLabelCharge.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabelCharge.setText("jLabel5");

        jLabel4.setText(bundle.getString("message.paymentgatewayext")); // NOI18N

        javax.swing.GroupLayout jPanelCashBackLayout = new javax.swing.GroupLayout(jPanelCashBack);
        jPanelCashBack.setLayout(jPanelCashBackLayout);
        jPanelCashBackLayout.setHorizontalGroup(
            jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCashBackLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(m_jKeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCashBackAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCashBackLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(96, Short.MAX_VALUE))
                    .addGroup(jPanelCashBackLayout.createSequentialGroup()
                        .addGroup(jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabelCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanelCashBackLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelCashBackLayout.setVerticalGroup(
            jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCashBackLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jCashBackAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCashBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCashBackLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPayment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jKeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(676, Short.MAX_VALUE))
        );

        add(jPanelCashBack);
        add(jLabel1);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCharge;
    private javax.swing.JLabel jLabelPayment;
    private javax.swing.JPanel jPanelCashBack;
    private java.awt.Label label1;
    private uk.chromis.editor.JEditorCurrencyPositive m_jCashBackAmount;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    // End of variables declaration//GEN-END:variables
    
}
